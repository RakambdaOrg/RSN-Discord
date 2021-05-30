package fr.raksrinana.rsndiscord.command2.api;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.permission.IPermission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.command2.permission.SimplePermission.TRUE_BY_DEFAULT;

public interface IExecutableCommand extends ICommand{
	default boolean replyEphemeral(){
		return true;
	}
	
	@NotNull
	CommandResult execute(@NotNull SlashCommandEvent event);
	
	@NotNull
	default IPermission getPermission(){
		return TRUE_BY_DEFAULT;
	}
	
	default boolean isSpecificAllowed(@NotNull Member member){
		return false;
	}
}
