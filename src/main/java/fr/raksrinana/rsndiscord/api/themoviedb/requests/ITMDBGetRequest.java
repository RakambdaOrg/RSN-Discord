package fr.raksrinana.rsndiscord.api.themoviedb.requests;

import kong.unirest.GetRequest;
import org.jetbrains.annotations.NotNull;

public interface ITMDBGetRequest<T> extends ITMDBRequest<T>{
	@NotNull
	GetRequest getRequest();
}
