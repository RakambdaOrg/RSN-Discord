package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 15/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public abstract class Configuration{
	/**
	 * The type of the configuration.
	 */
	public enum ConfigType{
		VALUE, LIST, MAP
	}
	
	/**
	 * Tells if the given action is allowed.
	 *
	 * @param action The action.
	 *
	 * @return True if allowed, false otherwise.
	 */
	public abstract boolean isActionAllowed(ConfigurationCommand.ChangeConfigType action);
	
	/**
	 * Handle the changes requested.
	 *
	 * @param event  The event that triggered this change.
	 * @param action The action to perform.
	 * @param args   The parameters.
	 *
	 * @return The result of this change.
	 *
	 * @throws Exception If anything too bad happened.
	 */
	public abstract ConfigurationCommand.ActionResult handleChange(MessageReceivedEvent event, ConfigurationCommand.ChangeConfigType action, LinkedList<String> args) throws Exception;
	
	/**
	 * Get the name of this configuration.
	 *
	 * @return The name.
	 */
	public abstract String getName();
	
	/**
	 * Get the type of this configuration.
	 *
	 * @return The type.
	 */
	public abstract ConfigType getType();
}
