package fr.raksrinana.rsndiscord.utils.eslgaming;

import kong.unirest.GetRequest;

public interface ESLGetRequest<T> extends ESLRequest<T>{
	GetRequest getRequest();
}
