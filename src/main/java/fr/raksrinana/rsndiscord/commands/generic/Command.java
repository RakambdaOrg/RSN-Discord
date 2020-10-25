package fr.raksrinana.rsndiscord.commands.generic;

import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.PermissionUtils;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

public interface Command extends Comparable<Command>{
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
	default boolean isAllowed(@NonNull final Member member){
		return PermissionUtils.isUserAllowed(member, getPermission());
	}
	
	default boolean deleteCommandMessageImmediately(){
		return true;
	}
	
	/**
	 * Get the permission required to execute this command.
	 *
	 * @return The permission.
	 */
	@NonNull
	IPermission getPermission();
	
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
		return this.getCommandStrings().get(0).compareTo(otherCommand.getCommandStrings().get(0));
	}
	
	/**
	 * Get the name of the command.
	 *
	 * @return The name.
	 */
	@NonNull String getName(@NonNull Guild guild);
	
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
	@NonNull String getDescription(@NonNull Guild guild);
	
	/**
	 * Get the parent command.
	 *
	 * @return The parent command.
	 */
	Command getParent();
}
