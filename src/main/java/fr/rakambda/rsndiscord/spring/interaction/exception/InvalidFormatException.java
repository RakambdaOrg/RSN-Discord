package fr.rakambda.rsndiscord.spring.interaction.exception;

import org.jspecify.annotations.NonNull;

public class InvalidFormatException extends InteractionException{
	public InvalidFormatException(@NonNull String value){
		super("Failed to parse '" + value + "', not proper format");
	}
}
