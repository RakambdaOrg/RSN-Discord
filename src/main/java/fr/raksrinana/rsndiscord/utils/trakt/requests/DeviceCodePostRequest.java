package fr.raksrinana.rsndiscord.utils.trakt.requests;

import fr.raksrinana.rsndiscord.utils.trakt.TraktPostRequest;
import fr.raksrinana.rsndiscord.utils.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.utils.trakt.responses.DeviceCode;
import kong.unirest.json.JSONObject;
import lombok.NonNull;

public class DeviceCodePostRequest implements TraktPostRequest<DeviceCode>{
	@Override
	public @NonNull JSONObject getBody(){
		final var data = new JSONObject();
		data.put("client_id", TraktUtils.getClientId());
		return data;
	}
	
	@Override
	public Class<? extends DeviceCode> getResultClass(){
		return DeviceCode.class;
	}
	
	@Override
	public @NonNull String getEndpoint(){
		return "/oauth/device/code";
	}
}
