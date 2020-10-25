package fr.raksrinana.rsndiscord.modules.series.themoviedb.requests;

import kong.unirest.GetRequest;

public interface ITMDBGetRequest<T> extends ITMDBRequest<T>{
	GetRequest getRequest();
}
