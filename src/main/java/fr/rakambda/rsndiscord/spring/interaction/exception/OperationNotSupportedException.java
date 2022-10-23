package fr.rakambda.rsndiscord.spring.interaction.exception;

import fr.rakambda.rsndiscord.spring.configuration.ConfigurationOperation;

public class OperationNotSupportedException extends InteractionException{
	public OperationNotSupportedException(ConfigurationOperation operation){
		super("Unsupported operation " + operation);
	}
}
