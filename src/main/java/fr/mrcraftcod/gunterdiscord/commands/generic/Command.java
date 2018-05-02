package fr.mrcraftcod.gunterdiscord.commands.generic;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public interface Command
{
	/**
	 * Get where this command can be executed.
	 *
	 * @return The scope or -5 to be accessible everywhere.
	 *
	 * @see ChannelType#getId()
	 */
	int getScope();
	
	/**
	 * Get the name of the command.
	 *
	 * @return The name.
	 */
	String getName();
	
	/**
	 * Get the command (what's after the prefix).
	 *
	 * @return The command.
	 */
	String getCommand();
	
	/**
	 * Get a description of the command.
	 *
	 * @return The description.
	 */
	String getCommandDescription();
	
	/**
	 * Handle the command.
	 *
	 * @param event The event that triggered this command.
	 * @param args  The arguments.
	 *
	 * @return The result of this execution.
	 *
	 * @throws Exception If something bad happened.
	 */
	CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception;
}
