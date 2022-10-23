package fr.rakambda.rsndiscord.spring.audio.exception;

import fr.rakambda.rsndiscord.spring.BotException;

public abstract class SchedulerException extends BotException{
	public SchedulerException(String message){
		super(message);
	}
	
	public SchedulerException(String message, Throwable cause){
		super(message, cause);
	}
}
