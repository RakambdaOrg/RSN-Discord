package fr.rakambda.rsndiscord.spring.interaction.exception;

import org.jetbrains.annotations.NotNull;

public class InvalidTimeFormatException extends InteractionException{
	public InvalidTimeFormatException(@NotNull String value){
		super("Failed to parse '" + value + "', not a proper time");
	}
}
