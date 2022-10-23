package fr.rakambda.rsndiscord.spring.interaction.exception;

import org.jetbrains.annotations.NotNull;

public class InvalidStateException extends InteractionException{
	public InvalidStateException(@NotNull String message){
		super(message);
	}
}
