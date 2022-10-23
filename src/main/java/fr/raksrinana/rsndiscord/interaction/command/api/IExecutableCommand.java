package fr.raksrinana.rsndiscord.interaction.command.api;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.permission.IPermission;
import fr.raksrinana.rsndiscord.interaction.command.permission.NoPermission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import org.jetbrains.annotations.NotNull;

public interface IExecutableCommand<T extends CommandInteraction> extends ICommand{
	default boolean replyEphemeral(){
		return true;
	}
	
	default boolean deferReply(){
		return true;
	}
	
	@NotNull
	default CommandResult executeGuild(@NotNull T event, @NotNull Guild guild, @NotNull Member member){
		return CommandResult.NOT_IMPLEMENTED;
	}
	
	@NotNull
	default CommandResult executeUser(@NotNull T event){
		return CommandResult.NOT_IMPLEMENTED;
	}
	
	@NotNull
	default IPermission getPermission(){
		return NoPermission.INSTANCE;
	}
	
	default boolean isSpecificAllowed(@NotNull Member member){
		return false;
	}
	
	default void autoCompleteGuild(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull Guild guild, @NotNull Member member){}
	
	default void autoCompleteUser(@NotNull CommandAutoCompleteInteractionEvent event){}
}
