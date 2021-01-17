package fr.raksrinana.rsndiscord.api.trakt.requests.oauth;

import fr.raksrinana.rsndiscord.api.trakt.TraktApi;
import fr.raksrinana.rsndiscord.api.trakt.model.auth.AccessToken;
import fr.raksrinana.rsndiscord.api.trakt.model.auth.DeviceCode;
import fr.raksrinana.rsndiscord.api.trakt.requests.ITraktPostRequest;
import kong.unirest.GenericType;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.jetbrains.annotations.NotNull;

public class DeviceTokenPostRequest implements ITraktPostRequest<AccessToken>{
	private final String deviceCode;
	
	public DeviceTokenPostRequest(@NotNull DeviceCode deviceCode){
		this(deviceCode.getDeviceCode());
	}
	
	public DeviceTokenPostRequest(@NotNull String deviceCode){
		this.deviceCode = deviceCode;
	}
	
	@Override
	@NotNull
	public GenericType<AccessToken> getOutputType(){
		return new GenericType<>(){};
	}
	
	@Override
	@NotNull
	public RequestBodyEntity getRequest(){
		return Unirest.post(TraktApi.API_URL + "/oauth/device/token").body(getBody());
	}
	
	@NotNull
	public JSONObject getBody(){
		var data = new JSONObject();
		data.put("code", deviceCode);
		data.put("client_id", TraktApi.getClientId());
		data.put("client_secret", TraktApi.getClientSecret());
		return data;
	}
}
