package fr.rakambda.rsndiscord.spring;

import org.jetbrains.annotations.NotNull;

public abstract class BotException extends Exception{
	public BotException(String message){
		super(message);
	}
	
	public BotException(String message, Throwable cause){
		super(message, cause);
	}
	
	@NotNull
	public String getFriendlyMessageKey(){
		return "tbd"; //TODO abstract method
	}
	
	@NotNull
	public Object[] getFriendlyMessageArgs(){
		return new Object[0]; //TODO abstract method
	}
}
