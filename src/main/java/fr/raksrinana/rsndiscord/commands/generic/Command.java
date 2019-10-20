package fr.raksrinana.rsndiscord.commands.generic;

import fr.raksrinana.rsndiscord.utils.Utilities;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.commands.generic.Command.AccessLevel.*;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public interface Command extends Comparable<Command>{
	/**
	 * The level required to access a command.
	 */
	enum AccessLevel{ADMIN, MODERATOR, ALL, CREATOR}
	
	/**
	 * Add entries into the help menu.
	 *
	 * @param guild   The guild.
	 * @param builder The help menu.
	 */
	void addHelp(@Nonnull Guild guild, @Nonnull EmbedBuilder builder);
	
	/**
	 * Tell if a member is allowed to run the command.
	 *
	 * @param member The member to test.
	 *
	 * @return True if allowed, false otherwise.
	 */
	default boolean isAllowed(@Nullable final Member member){
		if(Objects.isNull(member)){
			return false;
		}
		if(this.getAccessLevel() == ALL){
			return true;
		}
		if(this.getAccessLevel() == MODERATOR && Utilities.isModerator(member)){
			return true;
		}
		if(this.getAccessLevel() == ADMIN && Utilities.isAdmin(member)){
			return true;
		}
		return Utilities.isCreator(member);
	}
	
	/**
	 * Get the level required to execute this command.
	 *
	 * @return The access level.
	 */
	@Nonnull
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
	@Nonnull
	CommandResult execute(@Nonnull GuildMessageReceivedEvent event, @Nonnull LinkedList<String> args) throws Exception;
	
	@Override
	default int compareTo(@Nonnull final Command otherCommand){
		return this.getName().compareTo(otherCommand.getName());
	}
	
	/**
	 * Get the name of the command.
	 *
	 * @return The name.
	 */
	@Nonnull
	String getName();
	
	/**
	 * Get the command (what's after the prefix).
	 *
	 * @return The command.
	 */
	@Nonnull
	List<String> getCommandStrings();
	
	/**
	 * Get a description of the usage of command.
	 *
	 * @return The description.
	 */
	@Nonnull
	String getCommandUsage();
	
	/**
	 * Get the description of the command.
	 *
	 * @return The description.
	 */
	@Nonnull
	String getDescription();
	
	/**
	 * Get the parent command.
	 *
	 * @return The parent command.
	 */
	@Nullable
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
