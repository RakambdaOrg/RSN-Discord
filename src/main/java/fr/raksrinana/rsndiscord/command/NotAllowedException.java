package fr.raksrinana.rsndiscord.command;

import lombok.NonNull;
import java.io.Serial;

public class NotAllowedException extends RuntimeException{
	@Serial
	private static final long serialVersionUID = -647603631725613643L;
	
	public NotAllowedException(@NonNull final String message){
		super(message);
	}
}
