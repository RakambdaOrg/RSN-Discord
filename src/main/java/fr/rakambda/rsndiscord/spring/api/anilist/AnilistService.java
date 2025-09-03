package fr.rakambda.rsndiscord.spring.api.anilist;

import fr.rakambda.rsndiscord.spring.api.HttpUtils;
import fr.rakambda.rsndiscord.spring.api.anilist.request.GraphQlRequest;
import fr.rakambda.rsndiscord.spring.api.anilist.request.TokenRequest;
import fr.rakambda.rsndiscord.spring.api.anilist.response.GqlResponse;
import fr.rakambda.rsndiscord.spring.api.anilist.response.MediaListPagedResponse;
import fr.rakambda.rsndiscord.spring.api.anilist.response.NotificationPagedResponse;
import fr.rakambda.rsndiscord.spring.api.anilist.response.PageResponse;
import fr.rakambda.rsndiscord.spring.api.anilist.response.TokenResponse;
import fr.rakambda.rsndiscord.spring.api.anilist.response.ViewerResponse;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.Error;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.MediaList;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.notification.Notification;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.notification.NotificationType;
import fr.rakambda.rsndiscord.spring.api.exceptions.AuthFailureException;
import fr.rakambda.rsndiscord.spring.api.exceptions.NotLoggedInException;
import fr.rakambda.rsndiscord.spring.api.exceptions.RequestFailedException;
import fr.rakambda.rsndiscord.spring.settings.ApplicationSettings;
import fr.rakambda.rsndiscord.spring.storage.entity.AnilistEntity;
import fr.rakambda.rsndiscord.spring.storage.repository.AnilistRepository;
import fr.rakambda.rsndiscord.spring.util.GraphQlService;
import fr.rakambda.rsndiscord.spring.util.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnilistService{
	private static final String API_URL = "https://anilist.co/api/v2";
	private static final String GRAPH_QL_URL = "https://graphql.anilist.co";
	
	private final WebClient apiClient;
	private final WebClient graphQlClient;
	private final ApplicationSettings settings;
	private final AnilistRepository anilistRepository;
	private final GraphQlService graphQlService;
	
	public AnilistService(ApplicationSettings settings, AnilistRepository anilistRepository, GraphQlService graphQlService){
		this.settings = settings;
		this.anilistRepository = anilistRepository;
		this.graphQlService = graphQlService;
		
		apiClient = WebClient.builder()
				.baseUrl(API_URL)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.filter(HttpUtils.logErrorFilter())
				.build();
		graphQlClient = WebClient.builder()
				.baseUrl(GRAPH_QL_URL)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.toString())
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.filter(HttpUtils.logErrorFilter(Set.of(HttpStatus.TOO_MANY_REQUESTS)))
				.build();
	}
	
	@NonNull
	public String getCodeLink(){
		return String.format("%s/oauth/authorize?client_id=%s&response_type=code&redirect_uri=%s", API_URL, settings.getAnilist().getAppId(), settings.getAnilist().getRedirectUri());
	}
	
	public void login(long userId, @NonNull String code) throws AuthFailureException, RequestFailedException{
		log.info("Getting access token for {}", userId);
		
		var response = HttpUtils.withStatusOkAndBody(apiClient.post()
				.uri(b -> b.pathSegment("oauth", "token").build())
				.bodyValue(TokenRequest.builder()
						.grantType("authorization_code")
						.clientId(settings.getAnilist().getAppId())
						.clientSecret(settings.getAnilist().getClientSecret())
						.redirectUri(settings.getAnilist().getRedirectUri())
						.code(code)
						.build())
				.retrieve()
				.toEntity(TokenResponse.class)
				.blockOptional()
				.orElseThrow(AuthFailureException::new));
		
		var entity = anilistRepository.findById(userId)
				.orElseGet(() -> AnilistEntity.builder()
						.id(userId)
						.lastNotificationDate(Instant.now())
						.lastMediaListDate(Instant.now())
						.build());
		
		entity.setAccessToken(response.getAccessToken());
		entity.setRefreshToken(response.getRefreshToken());
		entity.setRefreshTokenExpire(Instant.now().plusSeconds(response.getExpiresIn()));
		entity.setUserId(getViewerId(response.getAccessToken()).getViewer().getId());
		entity.setEnabled(true);
		
		anilistRepository.save(entity);
	}
	
	@NonNull
	private AnilistEntity getEntity(long userId) throws NotLoggedInException{
		log.trace("Getting previous access token for {}", userId);
		return anilistRepository.findById(userId)
				.filter(e -> Objects.nonNull(e.getAccessToken()))
				.orElseThrow(NotLoggedInException::new);
	}
	
	@NonNull
	private String getUserToken(long userId) throws NotLoggedInException{
		var entity = getEntity(userId);
		if(entity.getRefreshTokenExpire().isAfter(Instant.now())){
			return entity.getAccessToken();
		}
		
		entity.setAccessToken(null);
		entity.setRefreshToken(null);
		entity.setRefreshTokenExpire(null);
		
		anilistRepository.save(entity);
		throw new NotLoggedInException();
	}
	
	@NonNull
	private ViewerResponse getViewerId(@NonNull String token) throws RequestFailedException{
		return gqlQuery(token, "api/anilist/query/viewer.gql", Map.of(), new ParameterizedTypeReference<>(){});
	}
	
	@NonNull
	public Collection<Notification> getAllNotifications(long userId, @NonNull Collection<NotificationType> types, boolean resetNotificationCount) throws RequestFailedException, NotLoggedInException{
		log.info("Getting notifications for {}", userId);
		
		Collection<Notification> elements = new LinkedList<>();
		NotificationPagedResponse response;
		var currentPage = 1;
		
		var token = getUserToken(userId);
		
		do{
			response = getNotifications(token, types, resetNotificationCount, currentPage).getPage();
			elements.addAll(response.getNotifications());
			currentPage++;
		}
		while(response.getPageInfo().getHasNextPage());
		
		return elements;
	}
	
	@NonNull
	private PageResponse<NotificationPagedResponse> getNotifications(@NonNull String token, @NonNull Collection<NotificationType> types, boolean resetNotificationCount, int page) throws RequestFailedException{
		var params = Map.of(
				"page", page,
				"perPage", 150,
				"typeIn", types,
				"resetNotificationCount", resetNotificationCount
		);
		return gqlQuery(token, "api/anilist/query/notification.gql", params, new ParameterizedTypeReference<>(){});
	}
	
	@NonNull
	public Collection<MediaList> getAllMediaList(long userId, int anilistUserId) throws NotLoggedInException, RequestFailedException{
		log.info("Getting media list for {}", userId);
		
		Collection<MediaList> elements = new LinkedList<>();
		MediaListPagedResponse response;
		var currentPage = 1;
		
		var token = getUserToken(userId);
		
		do{
			response = getMediaList(token, anilistUserId, currentPage).getPage();
			elements.addAll(response.getMediaList());
			currentPage++;
		}
		while(response.getPageInfo().getHasNextPage());
		
		return elements;
	}
	
	@NonNull
	private PageResponse<MediaListPagedResponse> getMediaList(@NonNull String token, long anilistUserId, int page) throws RequestFailedException{
		var params = Map.<String, Object> of(
				"page", page,
				"perPage", 150,
				"userId", anilistUserId
		);
		return gqlQuery(token, "api/anilist/query/mediaList.gql", params, new ParameterizedTypeReference<>(){});
	}
	
	@NonNull
	private <T> T gqlQuery(@NonNull String token, @NonNull String definition, @NonNull Map<String, Object> variables, @NonNull ParameterizedTypeReference<GqlResponse<T>> type) throws RequestFailedException{
		try{
			log.debug("Sending gql query from definition {}", definition);
			
			var gqlQuery = GraphQlRequest.builder()
					.query(graphQlService.readQuery(definition))
					.variables(variables)
					.build();
			
			log.info("Sending gql query {}", gqlQuery);
			
			var response = HttpUtils.withStatusOkAndBody(graphQlClient.post()
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
					.bodyValue(gqlQuery)
					.retrieve()
					.toEntity(type)
					.retryWhen(Retry.indefinitely()
							.filter(WebClientResponseException.TooManyRequests.class::isInstance)
							.doBeforeRetryAsync(signal -> Mono.delay(calculateDelay(signal.failure())).then()))
					.blockOptional()
					.orElseThrow(RequestFailedException::new));
			
			if(response.getErrors().isEmpty()){
				return response.getData();
			}
			
			throw new RequestFailedException(response.getErrors().stream().map(Error::getMessage).collect(Collectors.joining(" | ")));
		}
		catch(ParseException e){
			throw new RequestFailedException("Failed to construct request", e);
		}
	}
	
	@NonNull
	private static Duration calculateDelay(@NonNull Throwable failure){
		if(!(failure instanceof WebClientResponseException.TooManyRequests webClientResponseException)){
			return Duration.ofMinutes(1);
		}
		
		var retryAfterHeader = webClientResponseException.getHeaders().getFirst("Retry-After");
		var retryAfter = Duration.ofSeconds(Optional.ofNullable(retryAfterHeader)
				.filter(s -> !s.isBlank())
				.map(Integer::parseInt)
				.orElse(60));
		
		log.warn("Anilist graphql api retry later : {}", retryAfter);
		return retryAfter;
	}
}
