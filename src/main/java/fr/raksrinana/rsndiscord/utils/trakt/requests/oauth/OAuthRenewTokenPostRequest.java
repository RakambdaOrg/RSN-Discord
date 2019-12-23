package fr.raksrinana.rsndiscord.utils.trakt.requests.oauth;

import fr.raksrinana.rsndiscord.settings.guild.trakt.TraktAccessTokenConfiguration;
import fr.raksrinana.rsndiscord.utils.trakt.TraktPostRequest;
import fr.raksrinana.rsndiscord.utils.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.utils.trakt.model.auth.AccessToken;
import kong.unirest.GenericType;
import kong.unirest.json.JSONObject;
import lombok.NonNull;

public class OAuthRenewTokenPostRequest implements TraktPostRequest<AccessToken>{
	private final String refreshToken;
	
	public OAuthRenewTokenPostRequest(@NonNull TraktAccessTokenConfiguration refreshToken){
		this(refreshToken.getRefreshToken());
	}
	
	public OAuthRenewTokenPostRequest(@NonNull String refreshToken){
		this.refreshToken = refreshToken;
	}
	
	@Override
	public JSONObject getBody(){
		final var data = new JSONObject();
		data.put("client_id", TraktUtils.getClientId());
		data.put("client_secret", TraktUtils.getClientSecret());
		data.put("grant_type", "refresh_token");
		data.put("redirect_uri", "urn:ietf:wg:oauth:2.0:oob");
		data.put("refresh_token", this.refreshToken);
		return data;
	}
	
	@Override
	public @NonNull String getEndpoint(){
		return "/oauth/token";
	}
	
	@Override
	public GenericType<? extends AccessToken> getResultClass(){
		return new GenericType<>(){};
	}
}
