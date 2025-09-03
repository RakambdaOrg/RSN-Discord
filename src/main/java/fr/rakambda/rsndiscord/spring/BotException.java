package fr.rakambda.rsndiscord.spring;

import org.jspecify.annotations.NonNull;

public abstract class BotException extends Exception{
	public BotException(String message){
		super(message);
	}
	
	public BotException(String message, Throwable cause){
		super(message, cause);
	}
	
	@NonNull
	public String getFriendlyMessageKey(){
		return "tbd"; //TODO abstract method
	}
	
	@NonNull
	public Object[] getFriendlyMessageArgs(){
		return new Object[0]; //TODO abstract method
	}
}
