package fr.mrcraftcod.gunterdiscord.commands.generic;

import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedList;
import java.util.List;
import static fr.mrcraftcod.gunterdiscord.commands.generic.Command.AccessLevel.*;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public interface Command extends Comparable<Command>
{
	/**
	 * The level required to access a command.
	 */
	enum AccessLevel
	{
		ADMIN, MODERATOR, ALL, CREATOR
	}
	
	/**
	 * Add entries into the help menu.
	 *
	 * @param guild   The guild.
	 * @param builder The help menu.
	 */
	void addHelp(Guild guild, EmbedBuilder builder);
	
	/**
	 * Tell if a member is allowed to run the command.
	 *
	 * @param member The member to test.
	 *
	 * @return True if allowed, false otherwise.
	 */
	default boolean isAllowed(Member member)
	{
		if(getAccessLevel() == ALL)
			return true;
		if(getAccessLevel() == MODERATOR && Utilities.isModerator(member))
			return true;
		if(getAccessLevel() == ADMIN && Utilities.isAdmin(member))
			return true;
		return Utilities.isCreator(member);
	}
	
	/**
	 * Get the level required to execute this command.
	 *
	 * @return The access level.
	 */
	AccessLevel getAccessLevel();
	
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
	
	@Override
	default int compareTo(@NotNull Command o)
	{
		return getName().compareTo(o.getName());
	}
	
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
	 * Get the parent command.
	 *
	 * @return The parent command.
	 */
	Command getParent();
	
	/**
	 * Get where this command can be executed.
	 *
	 * @return The scope or -5 to be accessible everywhere.
	 *
	 * @see ChannelType#getId()
	 */
	int getScope();
}
