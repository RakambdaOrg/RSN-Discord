package fr.raksrinana.rsndiscord.command2.api;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.permission.IPermission;
import fr.raksrinana.rsndiscord.command2.permission.SimplePermission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;

public interface IExecutableCommand extends ICommand{
	default boolean replyEphemeral(){
		return true;
	}
	
	@NotNull
	CommandResult execute(@NotNull SlashCommandEvent event);
	
	@NotNull
	default IPermission getPermission(){
		return SimplePermission.TRUE_BY_DEFAULT;
	}
}
