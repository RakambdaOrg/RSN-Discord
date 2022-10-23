package fr.rakambda.rsndiscord.spring.api.exceptions;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

public class StatusCodeException extends RequestFailedException{
	public StatusCodeException(@NotNull HttpStatus status){
		super("Invalid status code" + status);
	}
}
