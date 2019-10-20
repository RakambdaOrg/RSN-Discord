package fr.raksrinana.rsndiscord.commands.generic;

import javax.annotation.Nonnull;

public class NotHandledException extends Exception{
	private static final long serialVersionUID = -8774837884610682898L;
	
	NotHandledException(@Nonnull final String message){
		super(message);
	}
}
