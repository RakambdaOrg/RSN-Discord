package fr.raksrinana.rsndiscord.api.themoviedb.requests;

import kong.unirest.GetRequest;

public interface ITMDBGetRequest<T> extends ITMDBRequest<T>{
	GetRequest getRequest();
}
