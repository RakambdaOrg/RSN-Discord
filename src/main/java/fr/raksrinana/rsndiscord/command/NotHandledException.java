package fr.raksrinana.rsndiscord.command;

import org.jetbrains.annotations.NotNull;
import java.io.Serial;

public class NotHandledException extends RuntimeException{
	@Serial
	private static final long serialVersionUID = -8774837884610682898L;
	
	NotHandledException(@NotNull String message){
		super(message);
	}
}
