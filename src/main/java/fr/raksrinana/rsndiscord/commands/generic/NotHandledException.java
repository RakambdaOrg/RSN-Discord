package fr.raksrinana.rsndiscord.commands.generic;

import lombok.NonNull;
import java.io.Serial;

public class NotHandledException extends RuntimeException{
	@Serial
	private static final long serialVersionUID = -8774837884610682898L;
	
	NotHandledException(@NonNull final String message){
		super(message);
	}
}
