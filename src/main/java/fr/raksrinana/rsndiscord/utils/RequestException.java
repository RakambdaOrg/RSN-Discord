package fr.raksrinana.rsndiscord.utils;

import lombok.Getter;

@Getter
public class RequestException extends Exception{
	private final int status;
	
	public RequestException(String message, int status){
		super(message);
		this.status = status;
	}
}
