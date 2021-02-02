package fr.raksrinana.rsndiscord.command;

import org.jetbrains.annotations.NotNull;
import java.io.Serial;

public class NotAllowedException extends RuntimeException{
	@Serial
	private static final long serialVersionUID = -647603631725613643L;
	
	public NotAllowedException(@NotNull String message){
		super(message);
	}
}
