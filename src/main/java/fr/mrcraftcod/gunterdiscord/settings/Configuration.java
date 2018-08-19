package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 15/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public abstract class Configuration{
	protected final Guild guild;
	
	/**
	 * The type of the configuration.
	 */
	public enum ConfigType{
		VALUE, LIST, MAP
	}
	
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	protected Configuration(final Guild guild){this.guild = guild;}
	
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
	 * Define what actions can be performed.
	 *
	 * @return A collection of actions.
	 */
	public abstract Collection<ConfigurationCommand.ChangeConfigType> getAllowedActions();
	
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
