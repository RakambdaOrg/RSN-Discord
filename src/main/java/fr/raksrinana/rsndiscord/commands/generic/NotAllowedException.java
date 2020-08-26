package fr.raksrinana.rsndiscord.commands.generic;

import lombok.NonNull;

public class NotAllowedException extends RuntimeException{
	private static final long serialVersionUID = -647603631725613643L;
	
	public NotAllowedException(@NonNull final String message){
		super(message);
	}
}
