package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
	public enum ConfigType{VALUE, LIST, MAP}
	
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	protected Configuration(@Nullable final Guild guild){this.guild = guild;}
	
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
	@Nonnull
	public abstract ConfigurationCommand.ActionResult handleChange(@Nonnull GuildMessageReceivedEvent event, @Nonnull ConfigurationCommand.ChangeConfigType action, @Nonnull LinkedList<String> args) throws Exception;
	
	/**
	 * Define what actions can be performed.
	 *
	 * @return A collection of actions.
	 */
	@Nonnull
	public abstract Collection<ConfigurationCommand.ChangeConfigType> getAllowedActions();
	
	/**
	 * Get the name of this configuration.
	 *
	 * @return The name.
	 */
	@Nonnull
	public abstract String getName();
	
	/**
	 * Get the type of this configuration.
	 *
	 * @return The type.
	 */
	@Nonnull
	public abstract ConfigType getType();
}
