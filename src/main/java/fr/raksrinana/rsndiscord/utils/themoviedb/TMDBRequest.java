package fr.raksrinana.rsndiscord.utils.themoviedb;

import kong.unirest.GenericType;
import java.util.Objects;

public interface TMDBRequest<T>{
	int HTTP_OK_200 = 200;
	
	default boolean isValidResult(int status){
		return Objects.equals(status, HTTP_OK_200);
	}
	
	GenericType<T> getOutputType();
}
