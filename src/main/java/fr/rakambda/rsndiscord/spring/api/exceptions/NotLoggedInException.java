package fr.rakambda.rsndiscord.spring.api.exceptions;

public class NotLoggedInException extends ApiException{
	public NotLoggedInException(){
		super("Not logged in");
	}
}
