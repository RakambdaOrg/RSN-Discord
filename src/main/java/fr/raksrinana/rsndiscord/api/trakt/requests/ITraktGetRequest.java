package fr.raksrinana.rsndiscord.api.trakt.requests;

import kong.unirest.GetRequest;

public interface ITraktGetRequest<T> extends ITraktRequest<T>{
	GetRequest getRequest();
}
