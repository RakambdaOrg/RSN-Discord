package fr.rakambda.rsndiscord.spring.interaction.exception;

public class NotAvailableInGuildException extends InteractionException{
	public NotAvailableInGuildException(){
		super("Not available in guilds");
	}
}
