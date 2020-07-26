package fr.raksrinana.rsndiscord.utils.giphy;

import kong.unirest.GenericType;
import kong.unirest.GetRequest;
import java.util.Objects;

public interface GiphyGetRequest<T>{
	int HTTP_OK_200 = 200;
	
	default boolean isValidResult(int status){
		return Objects.equals(status, HTTP_OK_200);
	}
	
	GenericType<T> getOutputType();
	
	GetRequest getRequest();
}
