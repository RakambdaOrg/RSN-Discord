package fr.raksrinana.rsndiscord.modules.series.trakt.requests;

import kong.unirest.RequestBodyEntity;

public interface ITraktPostRequest<T> extends ITraktRequest<T>{
	RequestBodyEntity getRequest();
}
