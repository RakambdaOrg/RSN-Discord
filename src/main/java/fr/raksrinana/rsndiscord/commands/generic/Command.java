package fr.raksrinana.rsndiscord.commands.generic;

import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.commands.generic.Command.AccessLevel.*;

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
	void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder);
	
	/**
	 * Tell if a member is allowed to run the command.
	 *
	 * @param member The member to test.
	 *
	 * @return True if allowed, false otherwise.
	 */
	default boolean isAllowed(final Member member){
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
		return Utilities.isCreator(member.getUser());
	}
	
	default boolean deleteCommandMessageImmediately(){
		return true;
	}
	
	/**
	 * Get the level required to execute this command.
	 *
	 * @return The access level.
	 */
	@NonNull
	default AccessLevel getAccessLevel(){
		return ALL;
	}
	
	/**
	 * Handle the command.
	 *
	 * @param event The event that triggered this command.
	 * @param args  The arguments.
	 *
	 * @return The result of this execution.
	 *
	 * @throws RuntimeException If something bad happened.
	 */
	@NonNull CommandResult execute(@NonNull GuildMessageReceivedEvent event, @NonNull LinkedList<String> args) throws RuntimeException;
	
	@Override
	default int compareTo(@NonNull final Command otherCommand){
		return this.getName().compareTo(otherCommand.getName());
	}
	
	/**
	 * Get the name of the command.
	 *
	 * @return The name.
	 */
	@NonNull String getName();
	
	/**
	 * Get the command (what's after the prefix).
	 *
	 * @return The command.
	 */
	@NonNull List<String> getCommandStrings();
	
	/**
	 * Get a description of the usage of command.
	 *
	 * @return The description.
	 */
	@NonNull String getCommandUsage();
	
	/**
	 * Get the description of the command.
	 *
	 * @return The description.
	 */
	@NonNull String getDescription();
	
	/**
	 * Get the parent command.
	 *
	 * @return The parent command.
	 */
	Command getParent();
}
