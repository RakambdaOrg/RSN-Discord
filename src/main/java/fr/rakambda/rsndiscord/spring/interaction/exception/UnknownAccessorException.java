package fr.rakambda.rsndiscord.spring.interaction.exception;

import org.jspecify.annotations.NonNull;

public class UnknownAccessorException extends InteractionException{
	public UnknownAccessorException(@NonNull String accessorNames){
		super("Unknown configuration. Available: " + accessorNames);
	}
}
