package fr.rakambda.rsndiscord.spring.interaction.exception;

import org.jetbrains.annotations.NotNull;

public class UnknownAccessorException extends InteractionException{
	public UnknownAccessorException(@NotNull String accessorNames){
		super("Unknown configuration. Available: " + accessorNames);
	}
}
