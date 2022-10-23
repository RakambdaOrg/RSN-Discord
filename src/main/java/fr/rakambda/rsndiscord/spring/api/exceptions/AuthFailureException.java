package fr.rakambda.rsndiscord.spring.api.exceptions;

public class AuthFailureException extends ApiException{
	public AuthFailureException(){
		super("Failed to authenticate");
	}
}
