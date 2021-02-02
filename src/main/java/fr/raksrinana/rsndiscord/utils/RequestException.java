package fr.raksrinana.rsndiscord.utils;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import java.io.Serial;

@Getter
public class RequestException extends Exception{
	@Serial
	private static final long serialVersionUID = -4387330169770212392L;
	private final int status;
	
	public RequestException(@NotNull String message, int status){
		super(message);
		this.status = status;
	}
}
