package fr.rakambda.rsndiscord.spring.interaction.exception;

import org.jspecify.annotations.NonNull;

public class InvalidStateException extends InteractionException{
	public InvalidStateException(@NonNull String message){
		super(message);
	}
}
