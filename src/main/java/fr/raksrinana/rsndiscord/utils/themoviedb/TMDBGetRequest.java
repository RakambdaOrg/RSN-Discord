package fr.raksrinana.rsndiscord.utils.themoviedb;

import kong.unirest.GetRequest;

public interface TMDBGetRequest<T> extends TMDBRequest<T>{
	GetRequest getRequest();
}
