package fr.raksrinana.rsndiscord.api.themoviedb.requests;

import kong.unirest.GenericType;
import org.jetbrains.annotations.NotNull;

public interface ITMDBRequest<T>{
	@NotNull
	GenericType<T> getOutputType();
}
