package fr.rakambda.rsndiscord.spring.api.exceptions;

import fr.rakambda.rsndiscord.spring.BotException;

public abstract class ApiException extends BotException{
	public ApiException(String message){
		super(message);
	}
	
	public ApiException(String message, Throwable cause){
		super(message, cause);
	}
}
