package fr.raksrinana.rsndiscord.commands.config;

import fr.raksrinana.rsndiscord.settings.ConfigurationOperation;
import javax.annotation.Nonnull;

public class IllegalOperationException extends Exception{
	private static final long serialVersionUID = 2509773357487010299L;
	
	public IllegalOperationException(@Nonnull final ConfigurationOperation operation){
		super("The operation " + operation.name() + " isn't supported for this configuration");
	}
}
