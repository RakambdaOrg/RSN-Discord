package fr.raksrinana.rsndiscord.modules.series.trakt;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.series.trakt.model.auth.DeviceCode;
import fr.raksrinana.rsndiscord.modules.series.trakt.requests.ITraktGetRequest;
import fr.raksrinana.rsndiscord.modules.series.trakt.requests.ITraktPagedGetRequest;
import fr.raksrinana.rsndiscord.modules.series.trakt.requests.ITraktPostRequest;
import fr.raksrinana.rsndiscord.modules.series.trakt.requests.oauth.DeviceTokenPostRequest;
import fr.raksrinana.rsndiscord.modules.series.trakt.requests.oauth.OAuthRenewTokenPostRequest;
import fr.raksrinana.rsndiscord.modules.series.trakt.requests.users.UserSettingsGetRequest;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.general.trakt.TraktAccessTokenConfiguration;
import fr.raksrinana.rsndiscord.utils.InvalidResponseException;
import fr.raksrinana.rsndiscord.utils.RequestException;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

public class TraktUtils{
	public static final String API_URL = "https://api.trakt.tv";
	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	private static String clientId;
	private static String clientSecret;
	
	public static <T> Set<T> getPagedQuery(TraktAccessTokenConfiguration token, @NonNull ITraktPagedGetRequest<T> traktRequest) throws RequestException, InvalidResponseException{
		var results = new HashSet<T>();
		var headers = getHeaders(token);
		var pageCount = 1;
		do{
			var handler = traktRequest.getRequest().headers(headers).asObject(traktRequest.getOutputType());
			if(handler.isSuccess() && traktRequest.isValidResult(handler.getStatus())){
				results.addAll(handler.getBody());
				pageCount = ofNullable(handler.getHeaders().getFirst("X-Pagination-Page-Count"))
						.map(Integer::parseInt)
						.orElseThrow(() -> new RequestException("No page count in header", handler.getStatus()));
				var finalRequest = traktRequest;
				traktRequest = ofNullable(handler.getHeaders().getFirst("X-Pagination-Page"))
						.map(Integer::parseInt)
						.map(page -> page + 1)
						.map(finalRequest::getForPage)
						.orElseThrow(() -> new RequestException("No page in header", handler.getStatus()));
			}
			else{
				if(handler.getStatus() == 503 || handler.getStatus() == 521){
					Log.getLogger(null).warn("Trakt replied with {} status", handler.getStatus());
					return Set.of();
				}
				handler.getParsingError().ifPresent(error -> {
					Utilities.reportException("Failed to parse Trakt response, http status " + handler.getStatus(), error);
					Log.getLogger(null).warn("Failed to parse Trakt response", error);
				});
				throw new RequestException("Error sending API request, HTTP code " + handler.getStatus() + " => " + handler.getBody() + "(" + handler.getParsingError() + ")", handler.getStatus());
			}
		}
		while(traktRequest.getPage() < pageCount);
		return results;
	}
	
	private static Map<String, String> getHeaders(TraktAccessTokenConfiguration token){
		var headers = new HashMap<String, String>();
		if(nonNull(token)){
			headers.put("Authorization", "Bearer " + token.getToken());
		}
		headers.put("Content-Type", "application/json");
		headers.put("trakt-api-key", getClientId());
		headers.put("trakt-api-version", "2");
		return headers;
	}
	
	public static String getClientId(){
		if(isNull(clientId)){
			clientId = System.getProperty("TRAKT_CLIENT_ID");
		}
		return clientId;
	}
	
	public static void pollDeviceToken(@NonNull GuildMessageReceivedEvent event, @NonNull DeviceCode deviceCode){
		var guild = event.getGuild();
		var channel = event.getChannel();
		var userToken = Settings.getGeneral().getTrakt().getAccessToken(event.getAuthor().getIdLong()).orElse(null);
		var expireDate = ZonedDateTime.now().plusSeconds(deviceCode.getExpiresIn());
		var sleepTime = deviceCode.getInterval() * 1000L;
		
		executor.submit(() -> {
			var deviceTokenQuery = new DeviceTokenPostRequest(deviceCode);
			var additionalDelay = 0;
			var retry = true;
			
			while(retry && ZonedDateTime.now().isBefore(expireDate)){
				try{
					Thread.sleep(sleepTime + additionalDelay);
				}
				catch(InterruptedException e){
					Log.getLogger(null).warn("Failed to sleep while waiting to pull device token");
				}
				
				try{
					var deviceToken = postQuery(userToken, deviceTokenQuery);
					Settings.getGeneral().getTrakt()
							.addAccessToken(new TraktAccessTokenConfiguration(event.getAuthor().getIdLong(),
									ZonedDateTime.now().plusSeconds(deviceToken.getExpiresIn()),
									deviceToken.getAccessToken(), deviceToken.getRefreshToken()));
					channel.sendMessage(translate(guild, "trakt.authenticated")).submit();
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
				channel.sendMessage(translate(guild, "trakt.authentication-failed")).submit();
			}
		});
	}
	
	public static <T> T postQuery(TraktAccessTokenConfiguration token, @NonNull ITraktPostRequest<T> traktRequest) throws RequestException{
		var request = traktRequest.getRequest().headers(getHeaders(token)).asObject(traktRequest.getOutputType());
		if(request.isSuccess() && traktRequest.isValidResult(request.getStatus())){
			return request.getBody();
		}
		throw new RequestException("Error sending API request, HTTP code " + request.getStatus() + " => " + request.getBody(), request.getStatus());
	}
	
	public static void stopAll(){
		executor.shutdown();
	}
	
	public static Optional<String> getUsername(@NonNull Member member){
		var traktConfig = Settings.getGeneral().getTrakt();
		var memberId = member.getIdLong();
		
		return traktConfig.getUserUsername(memberId)
				.or(() -> getAccessToken(member).map(token -> {
					try{
						var userSettingsQuery = new UserSettingsGetRequest();
						var userSettings = getQuery(token, userSettingsQuery)
								.orElseThrow(() -> new RequestException("No user found", 0));
						var username = userSettings.getUser().getIds().getSlug();
						traktConfig.setUsername(memberId, username);
						return username;
					}
					catch(RequestException e){
						Log.getLogger(null).error("Failed to renew token", e);
					}
					return null;
				}));
	}
	
	public static <T> Optional<T> getQuery(TraktAccessTokenConfiguration token, @NonNull ITraktGetRequest<T> traktRequest) throws RequestException{
		final var request = traktRequest.getRequest().headers(getHeaders(token)).asObject(traktRequest.getOutputType());
		if(request.isSuccess() && traktRequest.isValidResult(request.getStatus())){
			return ofNullable(request.getBody());
		}
		if(request.getStatus() == 503 || request.getStatus() == 521){
			Log.getLogger(null).warn("Trakt replied with {} status", request.getStatus());
			return Optional.empty();
		}
		request.getParsingError().ifPresent(error -> {
			Utilities.reportException("Failed to parse Trakt response, http status " + request.getStatus(), error);
			Log.getLogger(null).warn("Failed to parse Trakt response", error);
		});
		throw new RequestException("Error sending API request, HTTP code " + request.getStatus() + " => " + request.getBody(), request.getStatus());
	}
	
	@NonNull
	private static Optional<TraktAccessTokenConfiguration> getAccessToken(@NonNull final Member member){
		var guild = member.getGuild();
		Log.getLogger(guild).debug("Getting previous access token for {}", member);
		var traktConfig = Settings.getGeneral().getTrakt();
		var accessTokenOptional = traktConfig.getAccessToken(member.getUser().getIdLong());
		
		if(accessTokenOptional.isPresent()){
			Log.getLogger(guild).debug("Found previous access token for {}", member);
			var accessToken = accessTokenOptional.get();
			if(accessToken.isExpired()){
				var newTokenOptional = renewToken(accessToken, member);
				if(newTokenOptional.isPresent()){
					traktConfig.removeAccessToken(accessToken);
				}
				return newTokenOptional;
			}
			return accessTokenOptional;
		}
		Log.getLogger(guild).debug("No access token found for {}", member);
		return Optional.empty();
	}
	
	private static Optional<TraktAccessTokenConfiguration> renewToken(@NonNull TraktAccessTokenConfiguration token, @NonNull Member member){
		var renewTokenQuery = new OAuthRenewTokenPostRequest(token);
		try{
			var accessToken = postQuery(null, renewTokenQuery);
			var newToken = new TraktAccessTokenConfiguration(member.getIdLong(), ZonedDateTime.now().plusSeconds(accessToken.getExpiresIn()),
					accessToken.getAccessToken(), accessToken.getRefreshToken());
			Settings.getGeneral().getTrakt().addAccessToken(newToken);
			return Optional.of(newToken);
		}
		catch(RequestException e){
			Log.getLogger(null).error("Failed to renew token", e);
		}
		return Optional.empty();
	}
	
	public static String getClientSecret(){
		if(isNull(clientSecret)){
			clientSecret = System.getProperty("TRAKT_CLIENT_SECRET");
		}
		return clientSecret;
	}
}
