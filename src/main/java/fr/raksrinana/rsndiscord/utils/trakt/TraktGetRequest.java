package fr.raksrinana.rsndiscord.utils.trakt;

import kong.unirest.GetRequest;

public interface TraktGetRequest<T> extends TraktRequest<T>{
	GetRequest getRequest();
}
