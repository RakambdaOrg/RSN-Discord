package fr.raksrinana.rsndiscord.utils.anilist;

import fr.raksrinana.rsndiscord.listeners.CommandsMessageListener;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.anilist.AnilistAccessTokenConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.InvalidResponseException;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.utils.http.requestssenders.post.JSONPostRequestSender;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class AniListUtils{
	private static final String API_URL = "https://anilist.co/api/v2";
	private static final int APP_ID = 1230;
	private static final String REDIRECT_URI = "https://anilist.co/api/v2/oauth/pin";
	@Getter
	private static final String CODE_LINK = String.format("%s/oauth/authorize?client_id=%d&response_type=code&redirect_uri=%s", API_URL, APP_ID, REDIRECT_URI);
	private static final String USER_INFO_QUERY = "query{Viewer {id name}}";
	private static final int HTTP_ERROR = 503;
	private static final int HTTP_TOO_MANY_REQUESTS = 429;
	public static URL FALLBACK_URL;
	
	public static void requestToken(@NonNull final Member member, @NonNull final String code) throws InvalidResponseException{
		Log.getLogger(member.getGuild()).debug("Getting access token for {}", member);
		final var accessToken = getAccessToken(member);
		if(accessToken.isPresent()){
			return;
		}
		final var headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		final var body = new JSONObject();
		body.put("grant_type", "authorization_code");
		body.put("client_id", "" + APP_ID);
		body.put("client_secret", System.getProperty("ANILIST_SECRET"));
		body.put("redirect_uri", REDIRECT_URI);
		body.put("code", code);
		final var result = new JSONPostRequestSender(Unirest.post(API_URL + "/oauth/token").headers(headers).body(body)).getRequestHandler();
		if(!result.getResult().isSuccess()){
			throw new InvalidResponseException("Getting token failed HTTP code " + result.getStatus());
		}
		final var json = result.getRequestResult().getObject();
		if(json.has("error") || !json.has("access_token") || !json.has("refresh_token")){
			throw new IllegalArgumentException("Getting token failed with error: " + json.getString("error"));
		}
		Settings.get(member.getGuild()).getAniListConfiguration().setRefreshToken(member.getUser().getIdLong(), json.getString("refresh_token"));
		Settings.get(member.getGuild()).getAniListConfiguration().addAccessToken(new AnilistAccessTokenConfiguration(member.getUser().getIdLong(), LocalDateTime.now().plusSeconds(json.getInt("expires_in")), json.getString("access_token")));
	}
	
	@NonNull
	private static Optional<AnilistAccessTokenConfiguration> getAccessToken(@NonNull final Member member){
		Log.getLogger(member.getGuild()).trace("Getting previous access token for {}", member);
		final var accessToken = Settings.get(member.getGuild()).getAniListConfiguration().getAccessToken(member.getUser().getIdLong());
		if(accessToken.isPresent()){
			Log.getLogger(member.getGuild()).trace("Found previous access token for {}", member);
			return accessToken;
		}
		Log.getLogger(member.getGuild()).debug("No access token found for {}", member);
		return Optional.empty();
	}
	
	public static Optional<Integer> getUserId(@NonNull final Member member){
		return Settings.get(member.getGuild()).getAniListConfiguration().getUserId(member.getUser().getIdLong()).map(Optional::of).orElseGet(() -> {
			try{
				final var userInfos = AniListUtils.postQuery(member, USER_INFO_QUERY, new JSONObject());
				final var userId = userInfos.getJSONObject("data").getJSONObject("Viewer").getInt("id");
				Settings.get(member.getGuild()).getAniListConfiguration().setUserId(member.getUser().getIdLong(), userId);
				return Optional.of(userId);
			}
			catch(final Exception e){
				Log.getLogger(member.getGuild()).error("Failed to get AniList user id for {}", member);
			}
			return Optional.empty();
		});
	}
	
	@NonNull
	public static JSONObject postQuery(@NonNull final Member member, @NonNull final String query, @NonNull final JSONObject variables) throws Exception{
		Log.getLogger(member.getGuild()).debug("Sending query to AniList for user {}", member.getUser());
		final var token = AniListUtils.getAccessToken(member).orElseThrow(() -> {
			Settings.get(member.getGuild()).getAniListConfiguration().removeUser(member.getUser());
			Actions.replyPrivate(member.getGuild(), member.getUser(), MessageFormat.format("Your token for AniList on {0} expired. Please use `{1}al r` to register again if you want to continue receiving information", member.getGuild().getName(), Settings.get(member.getGuild()).getPrefix().orElse(CommandsMessageListener.defaultPrefix)), null);
			return new IllegalStateException("No valid token found, please register again");
		});
		return postQuery(token, query, variables);
	}
	
	@NonNull
	private static JSONObject postQuery(@NonNull final AnilistAccessTokenConfiguration token, @NonNull final String query, @NonNull final JSONObject variables) throws Exception{
		final var headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + token.getToken());
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		final var body = new JSONObject();
		body.put("query", query);
		body.put("variables", variables);
		final var handler = new JSONPostRequestSender(Unirest.post("https://graphql.anilist.co").headers(headers).body(body)).getRequestHandler();
		if(handler.getResult().isSuccess()){
			return handler.getRequestResult().getObject();
		}
		if(Objects.equals(handler.getStatus(), HTTP_ERROR) || Objects.equals(handler.getStatus(), HTTP_TOO_MANY_REQUESTS)){
			try{
				final var result = handler.getRequestResult().getObject();
				if(result.has("errors")){
					final var errors = result.getJSONArray("errors");
					if(!errors.isEmpty()){
						throw new AnilistException(handler.getStatus(), errors.getJSONObject(0).getString("message"));
					}
				}
			}
			catch(final Exception ignored){
			}
		}
		throw new Exception("Error sending API request, HTTP code " + handler.getStatus() + " => " + handler.getRequestResult().toString() + " | query was " + query);
	}
	
	public static LocalDateTime getDefaultDate(){
		return LocalDateTime.of(2019, 7, 7, 0, 0);
	}
	
	static{
		try{
			FALLBACK_URL = new URL("https://anilist.co");
		}
		catch(final MalformedURLException e){
			Log.getLogger(null).error("Failed to create default URL", e);
		}
	}
}
