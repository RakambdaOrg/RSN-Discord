package fr.raksrinana.rsndiscord.command;

import fr.raksrinana.rsndiscord.permission.IPermission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.DeleteMode.BEFORE;

public interface Command extends Comparable<Command>{
	/**
	 * Add entries into the help menu.
	 *
	 * @param guild   The guild.
	 * @param builder The help menu.
	 */
	void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder);
	
	/**
	 * Tell if a member is allowed to run the command.
	 *
	 * @param member The member to test.
	 *
	 * @return True if allowed, false otherwise.
	 */
	default boolean isAllowed(@NotNull Member member){
		return getPermission().isAllowed(member);
	}
	
	/**
	 * Get the permission required to execute this command.
	 *
	 * @return The permission.
	 */
	@NotNull
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
	@NotNull
	CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args) throws RuntimeException;
	
	@Override
	default int compareTo(@NotNull Command otherCommand){
		return getCommandStrings().get(0).compareTo(otherCommand.getCommandStrings().get(0));
	}
	
	/**
	 * Get the command (what's after the prefix).
	 *
	 * @return The command.
	 */
	@NotNull
	List<String> getCommandStrings();
	
	/**
	 * Get the name of the command.
	 *
	 * @return The name.
	 */
	@NotNull
	String getName(@NotNull Guild guild);
	
	/**
	 * Get the description of the command.
	 *
	 * @return The description.
	 */
	@NotNull
	String getDescription(@NotNull Guild guild);
	
	/**
	 * Get a description of the usage of command.
	 *
	 * @return The description.
	 */
	@NotNull
	String getCommandUsage();
	
	@NotNull
	default DeleteMode getDeleteMode(){
		return BEFORE;
	}
	
	/**
	 * Get the parent command.
	 *
	 * @return The parent command.
	 */
	@Nullable
	Command getParent();
}
