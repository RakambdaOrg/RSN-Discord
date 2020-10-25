package fr.raksrinana.rsndiscord.modules.series.trakt.requests;

import kong.unirest.GetRequest;

public interface ITraktGetRequest<T> extends ITraktRequest<T>{
	GetRequest getRequest();
}
