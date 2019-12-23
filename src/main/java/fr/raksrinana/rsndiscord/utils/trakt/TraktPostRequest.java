package fr.raksrinana.rsndiscord.utils.trakt;

import kong.unirest.json.JSONObject;

public interface TraktPostRequest<T> extends TraktRequest<T>{
	JSONObject getBody();
}
