package fr.raksrinana.rsndiscord.utils.trakt;

import kong.unirest.RequestBodyEntity;

public interface TraktPostRequest<T> extends TraktRequest<T>{
	RequestBodyEntity getRequest();
}
