package fr.rakambda.rsndiscord.spring.api.simkl;

import fr.rakambda.rsndiscord.spring.api.HttpUtils;
import fr.rakambda.rsndiscord.spring.api.exceptions.NotLoggedInException;
import fr.rakambda.rsndiscord.spring.api.exceptions.RequestFailedException;
import fr.rakambda.rsndiscord.spring.api.simkl.response.AccessTokenResponse;
import fr.rakambda.rsndiscord.spring.api.simkl.response.DeviceCodeResponse;
import fr.rakambda.rsndiscord.spring.api.simkl.response.history.UserHistory;
import fr.rakambda.rsndiscord.spring.api.simkl.response.history.UserHistoryResponse;
import fr.rakambda.rsndiscord.spring.settings.ApplicationSettings;
import fr.rakambda.rsndiscord.spring.storage.entity.SimklEntity;
import fr.rakambda.rsndiscord.spring.storage.repository.SimklRepository;
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
public class SimklService{
	private static final String API_URL = "https://api.simkl.com";
	
	private final WebClient client;
	private final ApplicationSettings applicationSettings;
	private final SimklRepository simklRepository;
	
	@Autowired
	public SimklService(ApplicationSettings applicationSettings, SimklRepository simklRepository){
		this.applicationSettings = applicationSettings;
		this.simklRepository = simklRepository;
		
		client = WebClient.builder()
				.baseUrl(API_URL)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.defaultHeader("simkl-api-key", applicationSettings.getSimkl().getClientId())
				.build();
	}
	
	@NotNull
	public DeviceCodeResponse getDeviceCode() throws RequestFailedException{
		log.info("Getting new device code");
		
		return HttpUtils.withStatusOkAndBody(client.get()
				.uri(b -> b.pathSegment("oauth", "pin")
						.queryParam("client_id", applicationSettings.getSimkl().getClientId())
						.build())
				.retrieve()
				.toEntity(DeviceCodeResponse.class)
				.blockOptional()
				.orElseThrow(() -> new RequestFailedException("Failed to get device code")));
	}
	
	@NotNull
	public List<UserHistory> getAllUserHistory(long userId, @Nullable Instant startDate) throws NotLoggedInException, RequestFailedException{
		log.info("Getting user history for {}", userId);
		
		var entity = getEntity(userId);
		
		var histories = new ArrayList<UserHistory>();
		
		var result = getUserHistory(entity, startDate);
		histories.addAll(result.getAnime());
		histories.addAll(result.getMovies());
		histories.addAll(result.getShows());
		
		return histories;
	}
	
	@NotNull
	private UserHistoryResponse getUserHistory(@NotNull SimklEntity entity, @Nullable Instant startDate) throws RequestFailedException{
		log.info("Getting user history");
		
		var startDateValue = Optional.ofNullable(startDate)
				.map(d -> ZonedDateTime.ofInstant(d, UTC))
				.map(d -> d.format(ISO_DATE_TIME));
		
		return HttpUtils.withStatusOkAndBody(
				client.get()
						.uri(b -> {
							b = b.pathSegment("sync", "all-items")
									.queryParam("extended", "full")
									.queryParam("episode_watched_at", "yes");
							
							if(startDateValue.isPresent()){
								b = b.queryParam("date_from", startDateValue.get());
							}
							
							return b.build();
						})
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + entity.getAccessToken())
						.retrieve()
						.toEntity(new ParameterizedTypeReference<UserHistoryResponse>(){})
						.blockOptional()
						.orElseThrow(() -> new RequestFailedException("Failed to get user history")));
	}
	
	public boolean isUserRegistered(long userId){
		try{
			getEntity(userId);
			return true;
		}
		catch(NotLoggedInException e){
			return false;
		}
	}
	
	@NotNull
	private SimklEntity getEntity(long userId) throws NotLoggedInException{
		log.trace("Getting previous access token for {}", userId);
		return simklRepository.findById(userId)
				.filter(e -> Objects.nonNull(e.getAccessToken()))
				.orElseThrow(NotLoggedInException::new);
	}
	
	@NotNull
	public CompletableFuture<SimklEntity> pollDeviceToken(long userId, @NotNull DeviceCodeResponse deviceCode){
		log.info("Polling device token every {} seconds for {} for code {} and user {}", deviceCode.getInterval(), deviceCode.getExpiresIn(), deviceCode.getDeviceCode(), userId);
		
		var retryCount = deviceCode.getExpiresIn() / deviceCode.getInterval();
		
		var entity = simklRepository.findById(userId).orElseGet(() -> SimklEntity.builder()
				.id(userId)
				.lastActivityDate(Instant.now())
				.build());
		
		return client.get()
				.uri(b -> b.pathSegment("oauth", "pin", "{deviceCode}")
						.queryParam("client_id", applicationSettings.getSimkl().getClientId())
						.build(deviceCode.getUserCode()))
				.retrieve()
				.bodyToMono(AccessTokenResponse.class)
				.flatMap(body -> {
					if(!Objects.equals("OK", body.getResult())){
						return Mono.error(new IllegalStateException("Response is not ok"));
					}
					
					return Mono.just(body);
				})
				.retryWhen(Retry.fixedDelay(retryCount, Duration.ofSeconds(deviceCode.getInterval())))
				.doOnNext(r -> {
					entity.setAccessToken(r.getAccessToken());
					entity.setEnabled(true);
				})
				.map(u -> entity)
				.toFuture()
				.thenApply(simklRepository::save);
	}
}
