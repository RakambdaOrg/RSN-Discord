package fr.mrcraftcod.gunterdiscord.utils.anilist;

import fr.mrcraftcod.gunterdiscord.settings.configs.AniListAccessTokenConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.AniListCodeConfig;
import fr.mrcraftcod.utils.http.requestssenders.post.JSONPostRequestSender;
import net.dv8tion.jda.core.entities.Member;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
public class AniListUtils{
	private static final Logger LOGGER = LoggerFactory.getLogger(AniListUtils.class);
	private static final String API_URL = "https://anilist.co/api/v2";
	private static final int APP_ID = 1230;
	private static final String REDIRECT_URI = "https://anilist.co/api/v2/oauth/pin";
	private static final String CODE_LINK = String.format("%s/oauth/authorize?client_id=%d&response_type=code&redirect_uri=%s", API_URL, APP_ID, REDIRECT_URI);
	
	public static JSONObject getQuery(final Member member, final String code, final String query, final JSONObject variables) throws Exception{
		LOGGER.info("Sending query to AniList", member.getUser());
		return getQuery(AniListUtils.getAccessToken(member, code), query, variables);
	}
	
	private static JSONObject getQuery(final String token, final String query, final JSONObject variables) throws Exception{
		final var headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + token);
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		final var body = new JSONObject();
		body.put("query", query);
		body.put("variables", variables);
		final var handler = new JSONPostRequestSender(new URL("https://graphql.anilist.co"), headers, new HashMap<>(), body.toString()).getRequestHandler();
		if(handler.getStatus() == 200){
			return handler.getRequestResult().getObject();
		}
		throw new Exception("Error sending API request, HTTP code " + handler.getStatus());
	}
	
	public static String getAccessToken(final Member member, final String token) throws Exception{
		LOGGER.info("Getting access token for {}", member);
		final var accessToken = getPreviousAccessToken(member);
		if(Objects.nonNull(accessToken)){
			return accessToken;
		}
		
		final var headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Accept", "application/json");
		
		final var body = new JSONObject();
		body.put("grant_type", "authorization_code");
		body.put("client_id", "" + APP_ID);
		body.put("client_secret", System.getenv("ANILIST_SECRET"));
		body.put("redirect_uri", REDIRECT_URI);
		body.put("code", token);
		LOGGER.info("{}", System.getenv("ANILIST_SECRET"));
		final var sender = new JSONPostRequestSender(new URL(API_URL + "/oauth/token"), headers, new HashMap<>(), body.toString());
		final var result = sender.getRequestHandler();
		if(result.getStatus() != 200){
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
		return json.getString("access_token");
	}
	
	private static String getPreviousAccessToken(final Member member){
		LOGGER.info("Getting previous access token for {}", member);
		final var accessTokens = new AniListAccessTokenConfig(member.getGuild());
		final var access = accessTokens.getValue(member.getUser().getIdLong());
		if(Objects.nonNull(access)){
			for(final var time : access.keySet()){
				LOGGER.info("Found previous access token for {}", member);
				//if(time < System.currentTimeMillis())
				return access.get(time);
				//accessTokens.deleteKeyValue(user.getIdLong(), time);
			}
		}
		LOGGER.info("No access token found for {}", member);
		return null;
	}
	
	public static JSONObject getQuery(final Member member, final String query, final JSONObject variables) throws Exception{
		LOGGER.info("Sending query to AniList", member.getUser());
		return getQuery(AniListUtils.getPreviousAccessToken(member), query, variables);
	}
	
	public static String getCodeLink(){
		return CODE_LINK;
	}
}
