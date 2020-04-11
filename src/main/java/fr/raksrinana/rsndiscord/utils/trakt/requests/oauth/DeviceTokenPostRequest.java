package fr.raksrinana.rsndiscord.utils.trakt.requests.oauth;

import fr.raksrinana.rsndiscord.utils.trakt.TraktPostRequest;
import fr.raksrinana.rsndiscord.utils.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.utils.trakt.model.auth.AccessToken;
import fr.raksrinana.rsndiscord.utils.trakt.model.auth.DeviceCode;
import kong.unirest.GenericType;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import lombok.NonNull;

public class DeviceTokenPostRequest implements TraktPostRequest<AccessToken>{
	private final String deviceCode;
	
	public DeviceTokenPostRequest(@NonNull DeviceCode deviceCode){
		this(deviceCode.getDeviceCode());
	}
	
	public DeviceTokenPostRequest(@NonNull String deviceCode){
		this.deviceCode = deviceCode;
	}
	
	@Override
	public GenericType<AccessToken> getOutputType(){
		return new GenericType<>(){};
	}
	
	@Override
	public RequestBodyEntity getRequest(){
		return Unirest.post(TraktUtils.API_URL + "/oauth/device/token").body(getBody());
	}
	
	public JSONObject getBody(){
		final var data = new JSONObject();
		data.put("code", deviceCode);
		data.put("client_id", TraktUtils.getClientId());
		data.put("client_secret", TraktUtils.getClientSecret());
		return data;
	}
}
