package fr.raksrinana.rsndiscord.api.trakt.requests;

import kong.unirest.core.GenericType;
import org.jetbrains.annotations.NotNull;

public interface ITraktRequest<T>{
	@NotNull
	GenericType<T> getOutputType();
}
