package fr.raksrinana.rsndiscord.api.trakt.requests;

import kong.unirest.RequestBodyEntity;

public interface ITraktPostRequest<T> extends ITraktRequest<T>{
	RequestBodyEntity getRequest();
}
