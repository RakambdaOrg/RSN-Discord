package fr.raksrinana.rsndiscord.utils.trakt;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.trakt.TraktAccessTokenConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.RequestException;
import fr.raksrinana.rsndiscord.utils.trakt.requests.DeviceTokenPostRequest;
import fr.raksrinana.rsndiscord.utils.trakt.responses.DeviceCode;
import fr.raksrinana.utils.http.requestssenders.get.ObjectGetRequestSender;
import fr.raksrinana.utils.http.requestssenders.post.ObjectPostRequestSender;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class TraktUtils{
	private static final String API_URL = "https://api.trakt.tv";
	private static String CLIENT_ID;
	private static String CLIENT_SECRET;
	private static ExecutorService executor = Executors.newSingleThreadExecutor();
	
	public static <T> T getQuery(TraktAccessTokenConfiguration token, @NonNull TraktGetRequest<T> request) throws RequestException, MalformedURLException, URISyntaxException{
		final var headers = getHeaders(token);
		final var handler = new ObjectGetRequestSender<>(request.getResultClass(), new URL(API_URL + request.getEndpoint()), headers, request.getParameters()).getRequestHandler();
		if(request.isValidResult(handler.getStatus())){
			return handler.getRequestResult();
		}
		throw new RequestException("Error sending API request, HTTP code " + handler.getStatus() + " => " + handler.getRequestResult().toString(), handler.getStatus());
	}
	
	private static Map<String, String> getHeaders(TraktAccessTokenConfiguration token){
		final var headers = new HashMap<String, String>();
		if(Objects.nonNull(token)){
			headers.put("Authorization", "Bearer " + token.getToken());
		}
		headers.put("Content-Type", "application/json");
		headers.put("trakt-api-key", getClientId());
		headers.put("trakt-api-version", "2");
		return headers;
	}
	
	public static String getClientId(){
		if(Objects.isNull(CLIENT_ID)){
			CLIENT_ID = System.getProperty("TRAKT_CLIENT_ID");
		}
		return CLIENT_ID;
	}
	
	public static void stopAll(){
		executor.shutdown();
	}
	
	public static void pollDeviceToken(@NonNull GuildMessageReceivedEvent event, @NonNull DeviceCode deviceCode){
		final var userToken = Settings.get(event.getGuild()).getTraktConfiguration().getAccessToken(event.getAuthor().getIdLong()).orElse(null);
		final var expireDate = LocalDateTime.now().plusSeconds(deviceCode.getExpiresIn());
		final var sleepTime = deviceCode.getInterval() * 1000L;
		executor.submit(() -> {
			final var deviceTokenQuery = new DeviceTokenPostRequest(deviceCode);
			var additionalDelay = 0;
			var retry = true;
			while(retry && LocalDateTime.now().isBefore(expireDate)){
				try{
					Thread.sleep(sleepTime + additionalDelay);
				}
				catch(InterruptedException e){
					log.warn("Failed to sleep while waiting to pull device token");
				}
				try{
					final var deviceToken = postQuery(userToken, deviceTokenQuery);
					Settings.get(event.getGuild()).getTraktConfiguration().addAccessToken(new TraktAccessTokenConfiguration(event.getAuthor().getIdLong(), LocalDateTime.now().plusSeconds(deviceToken.getExpiresIn()), deviceToken.getAccessToken(), deviceToken.getRefreshToken()));
					Actions.reply(event, "Successfully authenticated", null);
					return;
				}
				catch(RequestException e){
					if(e.getStatus() == 400){
						continue;
					}
					if(e.getStatus() == 429){
						additionalDelay = 1000;
						continue;
					}
					retry = false;
				}
				catch(MalformedURLException | URISyntaxException e){
					log.error("Failed to get device token", e);
					retry = false;
				}
				Actions.reply(event, "Error while authenticating, please try again", null);
			}
		});
	}
	
	public static <T> T postQuery(TraktAccessTokenConfiguration token, @NonNull TraktPostRequest<T> request) throws RequestException, MalformedURLException, URISyntaxException{
		final var headers = getHeaders(token);
		final var handler = new ObjectPostRequestSender<>(request.getResultClass(), new URL(API_URL + request.getEndpoint()), headers, new HashMap<>(), request.getBody().toString()).getRequestHandler();
		if(request.isValidResult(handler.getStatus())){
			return handler.getRequestResult();
		}
		throw new RequestException("Error sending API request, HTTP code " + handler.getStatus() + " => " + Optional.ofNullable(handler.getRequestResult()).map(Object::toString).orElse(null), handler.getStatus());
	}
	
	public static String getClientSecret(){
		if(Objects.isNull(CLIENT_SECRET)){
			CLIENT_SECRET = System.getProperty("TRAKT_CLIENT_SECRET");
		}
		return CLIENT_SECRET;
	}
}
