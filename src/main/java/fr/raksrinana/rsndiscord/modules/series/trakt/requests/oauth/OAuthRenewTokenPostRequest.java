package fr.raksrinana.rsndiscord.modules.series.trakt.requests.oauth;

import fr.raksrinana.rsndiscord.modules.series.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.modules.series.trakt.model.auth.AccessToken;
import fr.raksrinana.rsndiscord.modules.series.trakt.requests.ITraktPostRequest;
import fr.raksrinana.rsndiscord.modules.settings.general.trakt.TraktAccessTokenConfiguration;
import kong.unirest.GenericType;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.NonNull;

public class OAuthRenewTokenPostRequest implements ITraktPostRequest<AccessToken>{
	private final String refreshToken;
	
	public OAuthRenewTokenPostRequest(@NonNull TraktAccessTokenConfiguration refreshToken){
		this(refreshToken.getRefreshToken());
	}
	
	public OAuthRenewTokenPostRequest(@NonNull String refreshToken){
		this.refreshToken = refreshToken;
	}
	
	@Override
	public GenericType<AccessToken> getOutputType(){
		return new GenericType<>(){};
	}
	
	@Override
	public RequestBodyEntity getRequest(){
		return Unirest.post(TraktUtils.API_URL + "/oauth/token").body(getBody());
	}
	
	public JSONObject getBody(){
		final var data = new JSONObject();
		data.put("client_id", TraktUtils.getClientId());
		data.put("client_secret", TraktUtils.getClientSecret());
		data.put("grant_type", "refresh_token");
		data.put("redirect_uri", "urn:ietf:wg:oauth:2.0:oob");
		data.put("refresh_token", refreshToken);
		return data;
	}
}
