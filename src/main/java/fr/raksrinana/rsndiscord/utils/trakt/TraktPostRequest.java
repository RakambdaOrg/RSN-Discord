package fr.raksrinana.rsndiscord.utils.trakt;

import kong.unirest.json.JSONObject;
import lombok.NonNull;

public interface TraktPostRequest<T> extends TraktRequest<T>{
	@NonNull JSONObject getBody();
}
