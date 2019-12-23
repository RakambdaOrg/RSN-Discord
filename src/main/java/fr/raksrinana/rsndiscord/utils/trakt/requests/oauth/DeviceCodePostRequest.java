package fr.raksrinana.rsndiscord.utils.trakt.requests.oauth;

import fr.raksrinana.rsndiscord.utils.trakt.TraktPostRequest;
import fr.raksrinana.rsndiscord.utils.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.utils.trakt.model.auth.DeviceCode;
import kong.unirest.GenericType;
import kong.unirest.json.JSONObject;
import lombok.NonNull;

public class DeviceCodePostRequest implements TraktPostRequest<DeviceCode>{
	@Override
	public JSONObject getBody(){
		final var data = new JSONObject();
		data.put("client_id", TraktUtils.getClientId());
		return data;
	}
	
	@Override
	public @NonNull String getEndpoint(){
		return "/oauth/device/code";
	}
	
	@Override
	public GenericType<? extends DeviceCode> getResultClass(){
		return new GenericType<>(){};
	}
}
