package fr.raksrinana.rsndiscord.api.trakt.requests;

import kong.unirest.core.RequestBodyEntity;
import org.jetbrains.annotations.NotNull;

public interface ITraktPostRequest<T> extends ITraktRequest<T>{
	@NotNull
	RequestBodyEntity getRequest();
}
