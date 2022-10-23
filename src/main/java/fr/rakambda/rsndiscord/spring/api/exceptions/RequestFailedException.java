package fr.rakambda.rsndiscord.spring.api.exceptions;

public class RequestFailedException extends ApiException{
	public RequestFailedException(){
		super("Request failed");
	}
	
	public RequestFailedException(String message){
		super(message);
	}
	
	public RequestFailedException(String message, Throwable cause){
		super(message, cause);
	}
}
