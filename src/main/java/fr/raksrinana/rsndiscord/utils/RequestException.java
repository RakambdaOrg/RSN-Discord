package fr.raksrinana.rsndiscord.utils;

import lombok.Getter;
import java.io.Serial;

@Getter
public class RequestException extends Exception{
	@Serial
	private static final long serialVersionUID = -4387330169770212392L;
	private final int status;
	
	public RequestException(String message, int status){
		super(message);
		this.status = status;
	}
}
