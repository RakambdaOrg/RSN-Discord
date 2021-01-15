package fr.raksrinana.rsndiscord.api.themoviedb.requests;

import kong.unirest.GenericType;
import java.util.Objects;

public interface ITMDBRequest<T>{
	int HTTP_OK_200 = 200;
	
	default boolean isValidResult(int status){
		return Objects.equals(status, HTTP_OK_200);
	}
	
	GenericType<T> getOutputType();
}
