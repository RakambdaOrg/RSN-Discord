package fr.raksrinana.rsndiscord.api.trakt;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.api.trakt.model.auth.DeviceCode;
import fr.raksrinana.rsndiscord.api.trakt.requests.ITraktGetRequest;
import fr.raksrinana.rsndiscord.api.trakt.requests.ITraktPagedGetRequest;
import fr.raksrinana.rsndiscord.api.trakt.requests.ITraktPostRequest;
import fr.raksrinana.rsndiscord.api.trakt.requests.oauth.DeviceTokenPostRequest;
import fr.raksrinana.rsndiscord.api.trakt.requests.oauth.OAuthRenewTokenPostRequest;
import fr.raksrinana.rsndiscord.api.trakt.requests.users.UserSettingsGetRequest;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.impl.general.trakt.TraktAccessTokenConfiguration;
import fr.raksrinana.rsndiscord.utils.InvalidResponseException;
import fr.raksrinana.rsndiscord.utils.RequestException;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Log4j2
public class TraktApi{
	public static final String API_URL = "https://api.trakt.tv";
	private static String clientId;
	private static String clientSecret;
	
	@NotNull
	public static <T> Set<T> getPagedQuery(@Nullable TraktAccessTokenConfiguration token, @NotNull ITraktPagedGetRequest<T> traktRequest) throws RequestException, InvalidResponseException{
		var results = new HashSet<T>();
		var headers = getHeaders(token);
		var pageCount = 1;
		do{
			var handler = traktRequest.getRequest().headers(headers).asObject(traktRequest.getOutputType());
			if(!handler.isSuccess()){
				if(handler.getStatus() == 503 || handler.getStatus() == 521){
					log.warn("Trakt replied with {} status", handler.getStatus());
					return Set.of();
				}
				handler.getParsingError().ifPresent(error -> {
					Utilities.reportException("Failed to parse Trakt response, http status " + handler.getStatus(), error);
					log.warn("Failed to parse Trakt response", error);
				});
				throw new RequestException("Error sending API request, HTTP code " + handler.getStatus() + " => " + handler.getBody() + "(" + handler.getParsingError() + ")", handler.getStatus());
			}
			
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
		while(traktRequest.getPage() < pageCount);
		return results;
	}
	
	@NotNull
	private static Map<String, String> getHeaders(@Nullable TraktAccessTokenConfiguration token){
		var headers = new HashMap<String, String>();
		if(nonNull(token)){
			headers.put("Authorization", "Bearer " + token.getToken());
		}
		headers.put("Content-Type", "application/json");
		headers.put("trakt-api-key", getClientId());
		headers.put("trakt-api-version", "2");
		return headers;
	}
	
	@Nullable
	public static String getClientId(){
		if(isNull(clientId)){
			clientId = System.getProperty("TRAKT_CLIENT_ID");
		}
		return clientId;
	}
	
	public static void pollDeviceToken(@NotNull SlashCommandInteractionEvent event, @NotNull DeviceCode deviceCode){
		var guild = event.getGuild();
		var channel = event.getChannel();
		var userToken = Settings.getGeneral().getTrakt().getAccessToken(event.getUser().getIdLong()).orElse(null);
		var expireDate = ZonedDateTime.now().plusSeconds(deviceCode.getExpiresIn());
		var sleepTime = deviceCode.getInterval() * 1000L;
		
		Main.getExecutorService().submit(() -> {
			var deviceTokenQuery = new DeviceTokenPostRequest(deviceCode);
			var additionalDelay = 0;
			var retry = true;
			
			while(retry && ZonedDateTime.now().isBefore(expireDate)){
				try{
					Thread.sleep(sleepTime + additionalDelay);
				}
				catch(InterruptedException e){
					log.warn("Failed to sleep while waiting to pull device token");
				}
				
				try{
					var deviceToken = postQuery(userToken, deviceTokenQuery);
					Settings.getGeneral().getTrakt()
							.addAccessToken(new TraktAccessTokenConfiguration(event.getUser().getIdLong(),
									ZonedDateTime.now().plusSeconds(deviceToken.getExpiresIn()),
									deviceToken.getAccessToken(), deviceToken.getRefreshToken()));
					
					JDAWrappers.edit(event, translate(guild, "trakt.authenticated")).submit();
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
				JDAWrappers.edit(event, translate(guild, "trakt.authentication-failed")).submit();
			}
		});
	}
	
	@NotNull
	public static <T> T postQuery(@Nullable TraktAccessTokenConfiguration token, @NotNull ITraktPostRequest<T> traktRequest) throws RequestException{
		var request = traktRequest.getRequest().headers(getHeaders(token)).asObject(traktRequest.getOutputType());
		if(!request.isSuccess()){
			throw new RequestException("Error sending API request, HTTP code " + request.getStatus() + " => " + request.getBody(), request.getStatus());
		}
		return request.getBody();
	}
	
	@NotNull
	public static Optional<String> getUsername(@NotNull Member member){
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
						log.error("Failed to renew token", e);
					}
					return null;
				}));
	}
	
	@NotNull
	public static <T> Optional<T> getQuery(@Nullable TraktAccessTokenConfiguration token, @NotNull ITraktGetRequest<T> traktRequest) throws RequestException{
		var request = traktRequest.getRequest().headers(getHeaders(token)).asObject(traktRequest.getOutputType());
		if(!request.isSuccess()){
			if(request.getStatus() == 503 || request.getStatus() == 521){
				log.warn("Trakt replied with {} status", request.getStatus());
				return Optional.empty();
			}
			
			request.getParsingError().ifPresent(error -> {
				Utilities.reportException("Failed to parse Trakt response, http status " + request.getStatus(), error);
				log.warn("Failed to parse Trakt response", error);
			});
			throw new RequestException("Error sending API request, HTTP code " + request.getStatus() + " => " + request.getBody(), request.getStatus());
		}
		return ofNullable(request.getBody());
	}
	
	@NotNull
	private static Optional<TraktAccessTokenConfiguration> getAccessToken(@NotNull Member member){
		var guild = member.getGuild();
		log.debug("Getting previous access token for {}", member);
		var traktConfig = Settings.getGeneral().getTrakt();
		var accessTokenOptional = traktConfig.getAccessToken(member.getUser().getIdLong());
		
		if(!accessTokenOptional.isPresent()){
			log.debug("No access token found for {}", member);
			return Optional.empty();
		}
		
		log.debug("Found previous access token for {}", member);
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
	
	@NotNull
	private static Optional<TraktAccessTokenConfiguration> renewToken(@NotNull TraktAccessTokenConfiguration token, @NotNull Member member){
		var renewTokenQuery = new OAuthRenewTokenPostRequest(token);
		try{
			var accessToken = postQuery(null, renewTokenQuery);
			var newToken = new TraktAccessTokenConfiguration(member.getIdLong(), ZonedDateTime.now().plusSeconds(accessToken.getExpiresIn()),
					accessToken.getAccessToken(), accessToken.getRefreshToken());
			Settings.getGeneral().getTrakt().addAccessToken(newToken);
			return Optional.of(newToken);
		}
		catch(RequestException e){
			log.error("Failed to renew token", e);
		}
		return Optional.empty();
	}
	
	@Nullable
	public static String getClientSecret(){
		if(isNull(clientSecret)){
			clientSecret = System.getProperty("TRAKT_CLIENT_SECRET");
		}
		return clientSecret;
	}
}
