package fr.rakambda.rsndiscord.spring.util;

import fr.rakambda.rsndiscord.spring.BotException;

public class ParseException extends BotException{
	public ParseException(String message){
		super(message);
	}
	
	public ParseException(String message, Throwable cause){
		super(message, cause);
	}
}
