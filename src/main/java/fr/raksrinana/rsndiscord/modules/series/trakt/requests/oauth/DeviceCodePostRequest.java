package fr.raksrinana.rsndiscord.modules.series.trakt.requests.oauth;

import fr.raksrinana.rsndiscord.modules.series.trakt.TraktUtils;
import fr.raksrinana.rsndiscord.modules.series.trakt.model.auth.DeviceCode;
import fr.raksrinana.rsndiscord.modules.series.trakt.requests.ITraktPostRequest;
import kong.unirest.GenericType;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

public class DeviceCodePostRequest implements ITraktPostRequest<DeviceCode>{
	@Override
	public GenericType<DeviceCode> getOutputType(){
		return new GenericType<>(){};
	}
	
	@Override
	public RequestBodyEntity getRequest(){
		return Unirest.post(TraktUtils.API_URL + "/oauth/device/code").body(getBody());
	}
	
	public static JSONObject getBody(){
		final var data = new JSONObject();
		data.put("client_id", TraktUtils.getClientId());
		return data;
	}
}
