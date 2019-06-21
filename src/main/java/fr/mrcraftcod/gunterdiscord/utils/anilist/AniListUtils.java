package fr.mrcraftcod.gunterdiscord.utils.anilist;

import fr.mrcraftcod.gunterdiscord.settings.configs.AniListAccessTokenConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListCodeConfig;
import fr.mrcraftcod.utils.http.requestssenders.post.JSONPostRequestSender;
import net.dv8tion.jda.api.entities.Member;
import org.json.JSONObject;
import javax.annotation.Nonnull;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
public class AniListUtils{
	private static final String API_URL = "https://anilist.co/api/v2";
	private static final int APP_ID = 1230;
	private static final String REDIRECT_URI = "https://anilist.co/api/v2/oauth/pin";
	private static final String CODE_LINK = String.format("%s/oauth/authorize?client_id=%d&response_type=code&redirect_uri=%s", API_URL, APP_ID, REDIRECT_URI);
	
	public static void generateToken(@Nonnull final Member member, @Nonnull final String token) throws Exception{
		getLogger(member.getGuild()).debug("Getting access token for {}", member);
		final var accessToken = getPreviousAccessToken(member);
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
		body.put("code", token);
		final var sender = new JSONPostRequestSender(new URL(API_URL + "/oauth/token"), headers, new HashMap<>(), body.toString());
		final var result = sender.getRequestHandler();
		if(!Objects.equals(result.getStatus(), 200)){
			throw new Exception("Getting token failed HTTP code " + result.getStatus());
		}
		final var json = result.getRequestResult().getObject();
		if(json.has("error") || !json.has("access_token") || !json.has("refresh_token")){
			throw new Exception("Getting token failed with error: " + json.getString("error"));
		}
		final var conf = new AniListCodeConfig(member.getGuild());
		final var accessTokens = new AniListAccessTokenConfig(member.getGuild());
		conf.addValue(member.getUser().getIdLong(), json.getString("refresh_token"));
		accessTokens.addValue(member.getUser().getIdLong(), System.currentTimeMillis() + json.getInt("expires_in") * 1000L, json.getString("access_token"));
		json.getString("access_token");
	}
	
	@Nonnull
	private static Optional<String> getPreviousAccessToken(@Nonnull final Member member){
		getLogger(member.getGuild()).debug("Getting previous access token for {}", member);
		final var accessTokens = new AniListAccessTokenConfig(member.getGuild());
		final var access = accessTokens.getValue(member.getUser().getIdLong());
		if(Objects.nonNull(access) && access.isPresent()){
			getLogger(member.getGuild()).debug("Found previous access token for {}", member);
			return access.get().entrySet().stream().filter(entry -> entry.getKey() > System.currentTimeMillis()).map(Map.Entry::getValue).findAny();
		}
		getLogger(member.getGuild()).debug("No access token found for {}", member);
		return Optional.empty();
	}
	
	@Nonnull
	public static JSONObject getQuery(@Nonnull final Member member, @Nonnull final String query, @Nonnull final JSONObject variables) throws Exception{
		getLogger(member.getGuild()).debug("Sending query to AniList for user {}", member.getUser());
		final var token = AniListUtils.getPreviousAccessToken(member).orElseThrow(() -> new IllegalStateException("No valid token found, please register again"));
		return getQuery(token, query, variables);
	}
	
	@Nonnull
	private static JSONObject getQuery(@Nonnull final String token, @Nonnull final String query, @Nonnull final JSONObject variables) throws Exception{
		final var headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + token);
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		final var body = new JSONObject();
		body.put("query", query);
		body.put("variables", variables);
		final var handler = new JSONPostRequestSender(new URL("https://graphql.anilist.co"), headers, new HashMap<>(), body.toString()).getRequestHandler();
		if(Objects.equals(handler.getStatus(), 200)){
			return handler.getRequestResult().getObject();
		}
		if(Objects.equals(handler.getStatus(), 503)){
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
	
	@Nonnull
	public static String getCodeLink(){
		return CODE_LINK;
	}
}
