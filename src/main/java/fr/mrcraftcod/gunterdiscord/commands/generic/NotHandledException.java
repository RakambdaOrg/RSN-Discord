package fr.mrcraftcod.gunterdiscord.commands.generic;

import javax.annotation.Nonnull;

public class NotHandledException extends Exception{
	public NotHandledException(@Nonnull final String message){
		super(message);
	}
}
