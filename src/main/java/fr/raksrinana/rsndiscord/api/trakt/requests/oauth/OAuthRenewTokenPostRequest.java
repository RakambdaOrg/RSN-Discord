package fr.raksrinana.rsndiscord.api.trakt.requests.oauth;

import fr.raksrinana.rsndiscord.api.trakt.TraktApi;
import fr.raksrinana.rsndiscord.api.trakt.model.auth.AccessToken;
import fr.raksrinana.rsndiscord.api.trakt.requests.ITraktPostRequest;
import fr.raksrinana.rsndiscord.settings.impl.general.trakt.TraktAccessTokenConfiguration;
import kong.unirest.GenericType;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.jetbrains.annotations.NotNull;

public class OAuthRenewTokenPostRequest implements ITraktPostRequest<AccessToken>{
	private final String refreshToken;
	
	public OAuthRenewTokenPostRequest(@NotNull TraktAccessTokenConfiguration refreshToken){
		this(refreshToken.getRefreshToken());
	}
	
	public OAuthRenewTokenPostRequest(@NotNull String refreshToken){
		this.refreshToken = refreshToken;
	}
	
	@Override
	@NotNull
	public GenericType<AccessToken> getOutputType(){
		return new GenericType<>(){};
	}
	
	@Override
	@NotNull
	public RequestBodyEntity getRequest(){
		return Unirest.post(TraktApi.API_URL + "/oauth/token").body(getBody());
	}
	
	@NotNull
	public JSONObject getBody(){
		var data = new JSONObject();
		data.put("client_id", TraktApi.getClientId());
		data.put("client_secret", TraktApi.getClientSecret());
		data.put("grant_type", "refresh_token");
		data.put("redirect_uri", "urn:ietf:wg:oauth:2.0:oob");
		data.put("refresh_token", refreshToken);
		return data;
	}
}
