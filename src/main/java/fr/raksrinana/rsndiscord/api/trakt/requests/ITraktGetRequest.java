package fr.raksrinana.rsndiscord.api.trakt.requests;

import kong.unirest.core.GetRequest;
import org.jetbrains.annotations.NotNull;

public interface ITraktGetRequest<T> extends ITraktRequest<T>{
	@NotNull
	GetRequest getRequest();
}
