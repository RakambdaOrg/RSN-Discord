package fr.raksrinana.rsndiscord.command2.api;

import fr.raksrinana.rsndiscord.command.CommandResult;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

public interface IExecutableCommand extends ICommand{
	default boolean replyEphemeral(){
		return true;
	}
	
	@NotNull
	CommandResult execute(@NotNull SlashCommandEvent event);
}
