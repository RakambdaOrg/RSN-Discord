package fr.raksrinana.rsndiscord.api.themoviedb.requests;

import kong.unirest.core.GenericType;
import org.jetbrains.annotations.NotNull;

public interface ITMDBRequest<T>{
	@NotNull
	GenericType<T> getOutputType();
}
