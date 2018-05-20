package fr.mrcraftcod.gunterdiscord.commands.generic;

import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.mrcraftcod.gunterdiscord.commands.generic.Command.AccessLevel.ALL;
import static fr.mrcraftcod.gunterdiscord.commands.generic.Command.AccessLevel.MODERATOR;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public interface Command
{
	/**
	 * The level required to access a command.
	 */
	enum AccessLevel
	{
		ADMIN, MODERATOR, ALL
	}
	
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
	List<String> getCommand();
	
	/**
	 * Get a description of the usage of command.
	 *
	 * @return The description.
	 */
	String getCommandUsage();
	
	/**
	 * Get the description of the command.
	 *
	 * @return The description.
	 */
	String getDescription();
	
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
	
	default boolean isAllowed(Member member)
	{
		if(getAccessLevel() == ALL)
			return true;
		if(getAccessLevel() == MODERATOR && Utilities.isModerator(member))
			return true;
		if(Utilities.isAdmin(member) || member.getUser().getIdLong() == 170119951498084352L)
			return true;
		return false;
	}
	
	/**
	 * Get the level required to execute this command.
	 *
	 * @return The access level.
	 */
	AccessLevel getAccessLevel();
}
