package fr.rakambda.rsndiscord.spring.interaction.exception;

import fr.rakambda.rsndiscord.spring.BotException;

public abstract class InteractionException extends BotException{
	public InteractionException(String message){
		super(message);
	}
	
	public InteractionException(String message, Throwable cause){
		super(message, cause);
	}
}
