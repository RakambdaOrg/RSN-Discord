package fr.raksrinana.rsndiscord.utils.trakt.requests;

import fr.raksrinana.rsndiscord.utils.trakt.TraktPostRequest;
import fr.raksrinana.rsndiscord.utils.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.utils.trakt.responses.DeviceCode;
import fr.raksrinana.rsndiscord.utils.trakt.responses.DeviceToken;
import kong.unirest.json.JSONObject;
import lombok.NonNull;

public class DeviceTokenPostRequest implements TraktPostRequest<DeviceToken>{
	private final String deviceCode;
	
	public DeviceTokenPostRequest(@NonNull DeviceCode deviceCode){
		this(deviceCode.getDeviceCode());
	}
	
	public DeviceTokenPostRequest(@NonNull String deviceCode){
		this.deviceCode = deviceCode;
	}
	
	@Override
	public @NonNull JSONObject getBody(){
		final var data = new JSONObject();
		data.put("code", deviceCode);
		data.put("client_id", TraktUtils.getClientId());
		data.put("client_secret", TraktUtils.getClientSecret());
		return data;
	}
	
	@Override
	public Class<? extends DeviceToken> getResultClass(){
		return DeviceToken.class;
	}
	
	@Override
	public @NonNull String getEndpoint(){
		return "/oauth/device/token";
	}
}
