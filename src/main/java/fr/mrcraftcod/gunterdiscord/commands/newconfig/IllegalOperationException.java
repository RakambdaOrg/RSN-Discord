package fr.mrcraftcod.gunterdiscord.commands.newconfig;

import fr.mrcraftcod.gunterdiscord.newSettings.ConfigurationOperation;
import javax.annotation.Nonnull;

public class IllegalOperationException extends Exception{
	public IllegalOperationException(@Nonnull ConfigurationOperation operation){
		super("The operation " + operation.name() + " isn't supported for this configuration");
	}
}
