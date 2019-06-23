package fr.mrcraftcod.gunterdiscord.commands.generic;

import javax.annotation.Nonnull;

public class NotHandledException extends Exception{
	NotHandledException(@Nonnull final String message){
		super(message);
	}
}
