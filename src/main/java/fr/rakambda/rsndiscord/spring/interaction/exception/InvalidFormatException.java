package fr.rakambda.rsndiscord.spring.interaction.exception;

import org.jetbrains.annotations.NotNull;

public class InvalidFormatException extends InteractionException{
	public InvalidFormatException(@NotNull String value){
		super("Failed to parse '" + value + "', not proper format");
	}
}
