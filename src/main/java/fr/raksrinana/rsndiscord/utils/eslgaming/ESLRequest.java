package fr.raksrinana.rsndiscord.utils.eslgaming;

import kong.unirest.GenericType;
import java.util.Objects;

public interface ESLRequest<T>{
	int HTTP_OK_200 = 200;
	
	default boolean isValidResult(int status){
		return Objects.equals(status, HTTP_OK_200);
	}
	
	GenericType<T> getOutputType();
}
