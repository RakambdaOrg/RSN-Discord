package fr.rakambda.rsndiscord.spring.interaction.exception;

public class NotAllowedException extends InteractionException{
	public NotAllowedException(){
		super("You're not allowed to use this command");
	}
}
