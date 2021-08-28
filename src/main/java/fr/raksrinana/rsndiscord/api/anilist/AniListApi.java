package fr.raksrinana.rsndiscord.api.anilist;

import fr.raksrinana.rsndiscord.api.anilist.data.TokenResponse;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.impl.general.anilist.AniListAccessTokenConfiguration;
import fr.raksrinana.rsndiscord.utils.InvalidResponseException;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Log4j2
public class AniListApi{
	private static final String API_URL = "https://anilist.co/api/v2";
	private static final String REDIRECT_URI = "https://anilist.co/api/v2/oauth/pin";
	private static final int APP_ID = 1230;
	@Getter
	private static final String CODE_LINK = String.format("%s/oauth/authorize?client_id=%d&response_type=code&redirect_uri=%s", API_URL, APP_ID, REDIRECT_URI);
	private static final String USER_INFO_QUERY = "query{Viewer {id name}}";
	private static final int HTTP_ERROR = 503;
	private static final int HTTP_TOO_MANY_REQUESTS = 429;
	
	public static URL FALLBACK_URL;
	
	public static void requestToken(@NotNull Member member, @NotNull String code) throws InvalidResponseException{
		log.debug("Getting access token for {}", member);
		
		var accessToken = getAccessToken(member);
		if(accessToken.isPresent()){
			return;
		}
		
		var headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		
		var body = new JSONObject();
		body.put("grant_type", "authorization_code");
		body.put("client_id", "" + APP_ID);
		body.put("client_secret", System.getProperty("ANILIST_SECRET"));
		body.put("redirect_uri", REDIRECT_URI);
		body.put("code", code);
		
		var request = Unirest.post(API_URL + "/oauth/token").headers(headers).body(body).asObject(TokenResponse.class);
		if(!request.isSuccess()){
			throw new InvalidResponseException("Getting token failed HTTP code " + request.getStatus());
		}
		var response = request.getBody();
		if(nonNull(response.getError()) || isNull(response.getAccessToken()) || isNull(response.getRefreshToken())){
			throw new IllegalArgumentException("Getting token failed with error: " + response.getError());
		}
		
		var aniListGeneral = Settings.getGeneral().getAniList();
		aniListGeneral.setRefreshToken(member.getUser().getIdLong(), response.getRefreshToken());
		aniListGeneral.addAccessToken(new AniListAccessTokenConfiguration(member.getUser().getIdLong(),
				ZonedDateTime.now().plusSeconds(response.getExpiresIn()),
				response.getAccessToken()));
	}
	
	@NotNull
	private static Optional<AniListAccessTokenConfiguration> getAccessToken(@NotNull Member member){
		log.trace("Getting previous access token for {}", member);
		
		var accessToken = Settings.getGeneral()
				.getAniList()
				.getAccessToken(member.getUser().getIdLong());
		if(accessToken.isPresent()){
			log.trace("Found previous access token for {}", member);
			return accessToken;
		}
		log.debug("No access token found for {}", member);
		return Optional.empty();
	}
	
	@NotNull
	public static Optional<Integer> getUserId(@NotNull Member member){
		var user = member.getUser();
		return Settings.getGeneral().getAniList()
				.getUserId(user.getIdLong())
				.or(() -> {
					try{
						var userInfos = AniListApi.postQuery(member, USER_INFO_QUERY, new JSONObject());
						var userId = userInfos.getJSONObject("data")
								.getJSONObject("Viewer")
								.getInt("id");
						Settings.getGeneral().getAniList().setUserId(user.getIdLong(), userId);
						return Optional.of(userId);
					}
					catch(Exception e){
						log.error("Failed to get AniList user id for {}", member);
					}
					return Optional.empty();
				});
	}
	
	@NotNull
	public static JSONObject postQuery(@NotNull Member member, @NotNull String query, @NotNull JSONObject variables) throws Exception{
		var guild = member.getGuild();
		var user = member.getUser();
		log.debug("Sending query to AniList for user {}", user);
		
		var token = AniListApi.getAccessToken(member).orElseThrow(() -> {
			Settings.getGeneral().getAniList().removeUserToken(user);
			var message = translate(guild, "anilist.token-expired", guild.getName(), "/external anilist register");
			user.openPrivateChannel().submit()
					.thenAccept(privateChannel -> privateChannel.sendMessage(message).submit());
			return new IllegalStateException("No valid token found, please register again");
		});
		return postQuery(token, query, variables);
	}
	
	@NotNull
	private static JSONObject postQuery(@NotNull AniListAccessTokenConfiguration token, @NotNull String query, @NotNull JSONObject variables) throws Exception{
		var headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + token.getToken());
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		var body = new JSONObject();
		body.put("query", query);
		body.put("variables", variables);
		
		var request = Unirest.post("https://graphql.anilist.co").headers(headers).body(body).asJson();
		if(request.isSuccess()){
			return request.getBody().getObject();
		}
		
		var requestStatus = request.getStatus();
		if(Objects.equals(requestStatus, HTTP_ERROR) || Objects.equals(requestStatus, HTTP_TOO_MANY_REQUESTS)){
			try{
				var result = request.getBody().getObject();
				if(result.has("errors")){
					var errors = result.getJSONArray("errors");
					if(!errors.isEmpty()){
						throw new AniListException(requestStatus, errors.getJSONObject(0).getString("message"));
					}
				}
			}
			catch(Exception ignored){
			}
		}
		throw new Exception("Error sending API request, HTTP code " + requestStatus + " => " + request.getBody() + " | query was " + query);
	}
	
	@NotNull
	public static ZonedDateTime getDefaultDate(){
		return ZonedDateTime.ofInstant(Instant.parse("2019-07-07T00:00:00Z"), ZoneId.of("UTC"));
	}
	
	static{
		try{
			FALLBACK_URL = new URL("https://anilist.co");
		}
		catch(MalformedURLException e){
			log.error("Failed to create default URL", e);
		}
	}
}
