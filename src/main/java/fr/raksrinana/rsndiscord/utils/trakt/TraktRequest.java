package fr.raksrinana.rsndiscord.utils.trakt;

import kong.unirest.GenericType;
import lombok.NonNull;
import java.util.Objects;

public interface TraktRequest<T>{
	int HTTP_OK_200 = 200;
	int HTTP_OK_201 = 201;
	
	default boolean isValidResult(int status){
		return Objects.equals(status, HTTP_OK_200);
	}
	
	@NonNull String getEndpoint();
	
	GenericType<? extends T> getResultClass();
}
