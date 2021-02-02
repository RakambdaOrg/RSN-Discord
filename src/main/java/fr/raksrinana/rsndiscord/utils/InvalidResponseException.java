package fr.raksrinana.rsndiscord.utils;

import org.jetbrains.annotations.NotNull;
import java.io.Serial;

/**
 * Exception used when an invalid response is received from an HTTP request.
 */
public class InvalidResponseException extends RuntimeException{
	@Serial
	private static final long serialVersionUID = -8781299726434309433L;
	
	/**
	 * @param reason The reason.
	 */
	public InvalidResponseException(@NotNull String reason){
		super(reason);
	}
}
