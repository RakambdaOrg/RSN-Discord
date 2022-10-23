package fr.rakambda.rsndiscord.spring.audio.exception;

import fr.rakambda.rsndiscord.spring.BotException;

public abstract class TrackLoadException extends BotException{
	public TrackLoadException(String message){
		super(message);
	}
	
	public TrackLoadException(String message, Throwable cause){
		super(message, cause);
	}
}
