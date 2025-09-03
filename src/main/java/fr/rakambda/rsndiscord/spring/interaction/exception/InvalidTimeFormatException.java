package fr.rakambda.rsndiscord.spring.interaction.exception;

import org.jspecify.annotations.NonNull;

public class InvalidTimeFormatException extends InteractionException{
	public InvalidTimeFormatException(@NonNull String value){
		super("Failed to parse '" + value + "', not a proper time");
	}
}
