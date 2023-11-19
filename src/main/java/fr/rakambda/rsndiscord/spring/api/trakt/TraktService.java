package fr.rakambda.rsndiscord.spring.api.trakt;

import fr.rakambda.rsndiscord.spring.api.HttpUtils;
import fr.rakambda.rsndiscord.spring.api.exceptions.NotLoggedInException;
import fr.rakambda.rsndiscord.spring.api.exceptions.RequestFailedException;
import fr.rakambda.rsndiscord.spring.api.trakt.request.DeviceCodeRequest;
import fr.rakambda.rsndiscord.spring.api.trakt.request.RenewTokenRequest;
import fr.rakambda.rsndiscord.spring.api.trakt.request.RequestTokenRequest;
import fr.rakambda.rsndiscord.spring.api.trakt.response.AccessTokenResponse;
import fr.rakambda.rsndiscord.spring.api.trakt.response.DeviceCodeResponse;
import fr.rakambda.rsndiscord.spring.api.trakt.response.UserHistoryResponse;
import fr.rakambda.rsndiscord.spring.api.trakt.response.UserSettingsResponse;
import fr.rakambda.rsndiscord.spring.api.trakt.response.data.history.UserHistory;
import fr.rakambda.rsndiscord.spring.settings.ApplicationSettings;
import fr.rakambda.rsndiscord.spring.storage.entity.TraktEntity;
import fr.rakambda.rsndiscord.spring.storage.repository.TraktRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

@Service
@Slf4j
public class TraktService{
	private static final String API_URL = "https://api.trakt.tv";
	
	private final WebClient client;
	private final ApplicationSettings applicationSettings;
	private final TraktRepository traktRepository;
	
	@Autowired
	public TraktService(ApplicationSettings applicationSettings, TraktRepository traktRepository){
		this.applicationSettings = applicationSettings;
		this.traktRepository = traktRepository;
		
		client = WebClient.builder()
				.baseUrl(API_URL)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.defaultHeader("trakt-api-key", applicationSettings.getTrakt().getClientId())
				.defaultHeader("trakt-api-version", "2")
				.build();
	}
	
	@NotNull
	public Mono<UserSettingsResponse> getUserSettings(@NotNull String token){
		return client.get()
				.uri(b -> b.pathSegment("users", "settings").build())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<>(){});
	}
	
	@NotNull
	public DeviceCodeResponse getDeviceCode() throws RequestFailedException{
		log.info("Getting new device code");
		
		return HttpUtils.withStatusOkAndBody(client.post()
				.uri(b -> b.pathSegment("oauth", "device", "code")
						.build())
				.bodyValue(new DeviceCodeRequest(applicationSettings.getTrakt().getClientId()))
				.retrieve()
				.toEntity(DeviceCodeResponse.class)
				.blockOptional()
				.orElseThrow(() -> new RequestFailedException("Failed to get device code")));
	}
	
	@NotNull
	public List<UserHistory> getAllUserHistory(long userId, @Nullable Instant startDate, @Nullable Instant endDate) throws NotLoggedInException, RequestFailedException{
		log.info("Getting user history for {}", userId);
		
		var entity = getEntity(userId);
		verifyAccessToken(entity);
		
		var histories = new ArrayList<UserHistory>();
		var page = 1;
		
		UserHistoryResponse result;
		do{
			result = getUserHistory(entity, page, startDate, endDate);
			histories.addAll(result.userHistories());
			page = result.page() + 1;
		}
		while(result.page() < result.maxPage());
		
		return histories;
	}
	
	@NotNull
	private UserHistoryResponse getUserHistory(@NotNull TraktEntity entity, int page, @Nullable Instant startDate, @Nullable Instant endDate) throws RequestFailedException{
		log.info("Getting user history at page {}", page);
		
		var startDateValue = Optional.ofNullable(startDate)
				.map(d -> ZonedDateTime.ofInstant(d, UTC))
				.map(d -> d.format(ISO_DATE_TIME));
		var endDateValue = Optional.ofNullable(endDate)
				.map(d -> ZonedDateTime.ofInstant(d, UTC))
				.map(d -> d.format(ISO_DATE_TIME));
		
		var response = HttpUtils.verifyBody(HttpUtils.verifyStatus(
				client.get()
						.uri(b -> {
							b = b.pathSegment("users", "{username}", "history")
									.queryParam("extended", "full")
									.queryParam("page", page)
									.queryParam("limit", 15);
							
							if(startDateValue.isPresent()){
								b = b.queryParam("start_at", startDateValue.get());
							}
							if(endDateValue.isPresent()){
								b = b.queryParam("end_at", endDateValue.get());
							}
							
							return b.build(entity.getUsername());
						})
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + entity.getAccessToken())
						.retrieve()
						.toEntity(new ParameterizedTypeReference<List<UserHistory>>(){})
						.blockOptional()
						.orElseThrow(() -> new RequestFailedException("Failed to get user history"))));
		
		return new UserHistoryResponse(
				response.getBody(),
				Integer.parseInt(response.getHeaders().getFirst("X-Pagination-Page")),
				Integer.parseInt(response.getHeaders().getFirst("X-Pagination-Page-Count"))
		);
	}
	
	public boolean isUserRegistered(long userId){
		try{
			verifyAccessToken(getEntity(userId));
			return true;
		}
		catch(NotLoggedInException e){
			return false;
		}
	}
	
	private void verifyAccessToken(@NotNull TraktEntity entity) throws NotLoggedInException{
		if(entity.getRefreshTokenExpire().isAfter(Instant.now())){
			return;
		}
		
		entity.setAccessToken(null);
		entity.setRefreshToken(null);
		entity.setRefreshTokenExpire(null);
		
		traktRepository.save(entity);
		throw new NotLoggedInException();
	}
	
	@NotNull
	private TraktEntity getEntity(long userId) throws NotLoggedInException{
		log.trace("Getting previous access token for {}", userId);
		return traktRepository.findById(userId)
				.filter(e -> Objects.nonNull(e.getAccessToken()))
				.orElseThrow(NotLoggedInException::new);
	}
	
	@NotNull
	public CompletableFuture<TraktEntity> pollDeviceToken(long userId, @NotNull DeviceCodeResponse deviceCode){
		log.info("Polling device token every {} seconds for {} for code {} and user {}", deviceCode.getInterval(), deviceCode.getExpiresIn(), deviceCode.getDeviceCode(), userId);
		
		var retryCount = deviceCode.getExpiresIn() / deviceCode.getInterval();
		
		var request = new RequestTokenRequest(
				applicationSettings.getTrakt().getClientId(),
				applicationSettings.getTrakt().getClientSecret(),
				deviceCode.getDeviceCode());
		
		var entity = traktRepository.findById(userId).orElseGet(() -> TraktEntity.builder()
				.id(userId)
				.lastActivityDate(Instant.now())
				.build());
		
		return client.post()
				.uri(b -> b.pathSegment("oauth", "device", "token").build())
				.bodyValue(request)
				.retrieve()
				.bodyToMono(AccessTokenResponse.class)
				.retryWhen(Retry.fixedDelay(retryCount, Duration.ofSeconds(deviceCode.getInterval())))
				.doOnNext(r -> {
					entity.setAccessToken(r.getAccessToken());
					entity.setRefreshToken(r.getRefreshToken());
					entity.setRefreshTokenExpire(Instant.now().plusSeconds(r.getExpiresIn()));
					entity.setEnabled(true);
				})
				.flatMap(r -> getUserSettings(r.getAccessToken()))
				.doOnNext(u -> entity.setUsername(u.getUser().getUsername()))
				.map(u -> entity)
				.toFuture()
				.thenApply(traktRepository::save);
	}
	
	public void renewToken(long userId) throws NotLoggedInException, RequestFailedException{
		log.info("Renewing token for {}", userId);
		
		var entity = getEntity(userId);
		
		var request = new RenewTokenRequest(
				applicationSettings.getTrakt().getClientId(),
				applicationSettings.getTrakt().getClientSecret(),
				"refresh_token",
				"urn:ietf:wg:oauth:2.0:oob",
				entity.getRefreshToken());
		
		var accessToken = HttpUtils.withStatusOkAndBody(client.post()
				.uri(b -> b.pathSegment("oauth", "token").build())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + entity.getAccessToken())
				.bodyValue(request)
				.retrieve()
				.toEntity(AccessTokenResponse.class)
				.blockOptional()
				.orElseThrow(() -> new RequestFailedException("Failed to renew access token")));
		
		entity.setAccessToken(accessToken.getAccessToken());
		entity.setRefreshToken(accessToken.getRefreshToken());
		entity.setRefreshTokenExpire(Instant.now().plusSeconds(accessToken.getExpiresIn()));
		
		traktRepository.save(entity);
	}
}
