package fr.raksrinana.rsndiscord.utils.themoviedb;

import kong.unirest.GenericType;
import lombok.NonNull;
import java.util.Objects;

public interface TMDBRequest<T>{
	int HTTP_OK_200 = 200;
	
	default boolean isValidResult(int status){
		return Objects.equals(status, HTTP_OK_200);
	}
	
	@NonNull String getEndpoint();
	
	GenericType<? extends T> getResultClass();
}
