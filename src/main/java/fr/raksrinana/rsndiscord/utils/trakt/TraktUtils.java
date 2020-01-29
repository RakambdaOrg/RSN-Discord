package fr.raksrinana.rsndiscord.utils.trakt;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.trakt.TraktAccessTokenConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.InvalidResponseException;
import fr.raksrinana.rsndiscord.utils.RequestException;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.trakt.model.auth.DeviceCode;
import fr.raksrinana.rsndiscord.utils.trakt.requests.oauth.DeviceTokenPostRequest;
import fr.raksrinana.rsndiscord.utils.trakt.requests.oauth.OAuthRenewTokenPostRequest;
import fr.raksrinana.rsndiscord.utils.trakt.requests.users.UserSettingsGetRequest;
import fr.raksrinana.utils.http.requestssenders.get.ObjectGetRequestSender;
import fr.raksrinana.utils.http.requestssenders.post.ObjectPostRequestSender;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TraktUtils{
	public static final String API_URL = "https://api.trakt.tv";
	private static String CLIENT_ID;
	private static String CLIENT_SECRET;
	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	
	public static <T> Set<T> getPagedQuery(TraktAccessTokenConfiguration token, @NonNull TraktPagedGetRequest<T> request) throws RequestException, InvalidResponseException{
		final var results = new HashSet<T>();
		final var headers = getHeaders(token);
		var pageCount = 1;
		do{
			final var handler = new ObjectGetRequestSender<>(request.getOutputType(), request.getRequest().headers(headers)).getRequestHandler();
			handler.getResult().getParsingError().ifPresent(error -> Log.getLogger(null).warn("Failed to parse response", error));
			if(handler.getResult().isSuccess() && request.isValidResult(handler.getStatus())){
				results.addAll(handler.getRequestResult());
				pageCount = Optional.ofNullable(handler.getHeaders().getFirst("X-Pagination-Page-Count")).map(Integer::parseInt).orElseThrow(() -> new RequestException("No page count in header", handler.getStatus()));
				@NonNull final var finalRequest = request;
				request = Optional.ofNullable(handler.getHeaders().getFirst("X-Pagination-Page")).map(Integer::parseInt).map(page -> page + 1).map(finalRequest::getForPage).orElseThrow(() -> new RequestException("No page in header", handler.getStatus()));
			}
			else{
				throw new RequestException("Error sending API request, HTTP code " + handler.getStatus() + " => " + handler.getRequestResult() + "(" + handler.getResult().getParsingError() + ")", handler.getStatus());
			}
		}
		while(request.getPage() < pageCount);
		return results;
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
					Log.getLogger(null).warn("Failed to sleep while waiting to pull device token");
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
				Actions.reply(event, "Error while authenticating, please try again", null);
			}
		});
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
	
	public static Optional<String> getUsername(@NonNull Member member){
		final var traktConfig = Settings.get(member.getGuild()).getTraktConfiguration();
		return traktConfig.getUserUsername(member.getIdLong()).or(() -> getAccessToken(member).map(token -> {
			try{
				final var userSettingsQuery = new UserSettingsGetRequest();
				final var userSettings = getQuery(token, userSettingsQuery);
				final var username = userSettings.getUser().getIds().getSlug();
				traktConfig.setUsername(member.getIdLong(), username);
				return username;
			}
			catch(RequestException e){
				Log.getLogger(null).error("Failed to renew token", e);
			}
			return null;
		}));
	}
	
	public static <T> T getQuery(TraktAccessTokenConfiguration token, @NonNull TraktGetRequest<T> request) throws RequestException{
		final var handler = new ObjectGetRequestSender<T>(request.getOutputType(), request.getRequest().headers(getHeaders(token))).getRequestHandler();
		handler.getResult().getParsingError().ifPresent(error -> Log.getLogger(null).warn("Failed to parse response", error));
		if(handler.getResult().isSuccess() && request.isValidResult(handler.getStatus())){
			return handler.getRequestResult();
		}
		throw new RequestException("Error sending API request, HTTP code " + handler.getStatus() + " => " + handler.getRequestResult().toString(), handler.getStatus());
	}
	
	public static String getClientSecret(){
		if(Objects.isNull(CLIENT_SECRET)){
			CLIENT_SECRET = System.getProperty("TRAKT_CLIENT_SECRET");
		}
		return CLIENT_SECRET;
	}
	
	@NonNull
	private static Optional<TraktAccessTokenConfiguration> getAccessToken(@NonNull final Member member){
		Log.getLogger(member.getGuild()).debug("Getting previous access token for {}", member);
		final var traktConfig = Settings.get(member.getGuild()).getTraktConfiguration();
		final var accessTokenOptional = traktConfig.getAccessToken(member.getUser().getIdLong());
		if(accessTokenOptional.isPresent()){
			Log.getLogger(member.getGuild()).debug("Found previous access token for {}", member);
			final var accessToken = accessTokenOptional.get();
			if(accessToken.isExpired()){
				final var newTokenOptional = renewToken(accessToken, member);
				if(newTokenOptional.isPresent()){
					traktConfig.removeAccessToken(accessToken);
				}
				return newTokenOptional;
			}
			return accessTokenOptional;
		}
		Log.getLogger(member.getGuild()).debug("No access token found for {}", member);
		return Optional.empty();
	}
	
	private static Optional<TraktAccessTokenConfiguration> renewToken(@NonNull TraktAccessTokenConfiguration token, @NonNull Member member){
		final var renewTokenQuery = new OAuthRenewTokenPostRequest(token);
		try{
			final var accessToken = postQuery(null, renewTokenQuery);
			final var newToken = new TraktAccessTokenConfiguration(member.getIdLong(), LocalDateTime.now().plusSeconds(accessToken.getExpiresIn()), accessToken.getAccessToken(), accessToken.getRefreshToken());
			Settings.get(member.getGuild()).getTraktConfiguration().addAccessToken(newToken);
			return Optional.of(newToken);
		}
		catch(RequestException e){
			Log.getLogger(null).error("Failed to renew token", e);
		}
		return Optional.empty();
	}
	
	public static <T> T postQuery(TraktAccessTokenConfiguration token, @NonNull TraktPostRequest<T> request) throws RequestException{
		final var handler = new ObjectPostRequestSender<>(request.getOutputType(), request.getRequest().headers(getHeaders(token))).getRequestHandler();
		if(handler.getResult().isSuccess() && request.isValidResult(handler.getStatus())){
			return handler.getRequestResult();
		}
		throw new RequestException("Error sending API request, HTTP code " + handler.getStatus() + " => " + Optional.ofNullable(handler.getRequestResult()).map(Object::toString).orElse(null), handler.getStatus());
	}
}
