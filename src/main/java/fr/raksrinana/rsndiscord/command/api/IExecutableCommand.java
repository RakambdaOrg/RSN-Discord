package fr.raksrinana.rsndiscord.command.api;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.permission.IPermission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.command.permission.SimplePermission.TRUE_BY_DEFAULT;

public interface IExecutableCommand extends ICommand{
	default boolean replyEphemeral(){
		return true;
	}
	
	@NotNull
	default CommandResult executeGuild(@NotNull SlashCommandEvent event, @NotNull Guild guild, @NotNull Member member){
		return CommandResult.NOT_IMPLEMENTED;
	}
	
	@NotNull
	default CommandResult executeUser(@NotNull SlashCommandEvent event){
		return CommandResult.NOT_IMPLEMENTED;
	}
	
	@NotNull
	default IPermission getPermission(){
		return TRUE_BY_DEFAULT;
	}
	
	default boolean isSpecificAllowed(@NotNull Member member){
		return false;
	}
}
