package fr.raksrinana.rsndiscord.interaction.command;

import fr.raksrinana.rsndiscord.event.EventListener;
import fr.raksrinana.rsndiscord.interaction.command.api.IExecutableCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.ExecutableSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.user.base.ExecutableUserCommand;
import fr.raksrinana.rsndiscord.log.LogContext;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@EventListener
@Log4j2
public class CommandListener extends ListenerAdapter{
	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event){
		super.onSlashCommandInteraction(event);
		
		try(var ignored = LogContext.with(event.getGuild()).with(event.getUser())){
			log.info("Received slash-command {} from {} with args {}", event.getCommandPath(), event.getUser(), getArgsForLogs(event.getOptions()));
			
			CommandService.getExecutableCommand(event.getCommandPath(), ExecutableSlashCommand.class).ifPresentOrElse(
					command -> event.deferReply(command.replyEphemeral()).submit().thenAccept(empty -> performInteraction(event, command)),
					() -> event.reply("Unknown command {%s".formatted(event.getCommandPath())).setEphemeral(true).submit());
		}
	}
	
	@Override
	public void onUserContextInteraction(@NotNull UserContextInteractionEvent event){
		super.onUserContextInteraction(event);
		
		try(var ignored = LogContext.with(event.getGuild()).with(event.getUser())){
			log.info("Received user-context {} from {}", event.getCommandPath(), event.getUser());
			
			CommandService.getExecutableCommand(event.getCommandPath(), ExecutableUserCommand.class).ifPresentOrElse(
					command -> event.deferReply(command.replyEphemeral()).submit().thenAccept(empty -> performInteraction(event, command)),
					() -> event.reply("Unknown command {%s".formatted(event.getCommandPath())).setEphemeral(true).submit());
		}
	}
	
	@Override
	public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event){
		super.onCommandAutoCompleteInteraction(event);
		
		try(var ignored = LogContext.with(event.getGuild()).with(event.getUser())){
			log.info("Received auto-complete {} from {} with args {}", event.getCommandPath(), event.getUser(), event.getFocusedOption());
			
			CommandService.getExecutableCommand(event.getCommandPath(), ExecutableSlashCommand.class).ifPresentOrElse(
					command -> performAutoComplete(event, command),
					() -> log.warn("Unknown command {%s".formatted(event.getCommandPath())));
		}
	}
	
	@NotNull
	private String getArgsForLogs(@NotNull List<OptionMapping> options){
		return options.stream()
				.map(option -> "%s(%s)[%s]".formatted(option.getName(), option.getType(), option.getAsString()))
				.collect(Collectors.joining(", "));
	}
	
	private <T extends CommandInteraction> void performInteraction(@NotNull T event, @NotNull IExecutableCommand<T> command){
		try{
			if(!checkPermission(event, command)){
				return;
			}
			
			CommandResult result;
			if(event.isFromGuild()){
				result = command.executeGuild(event, Objects.requireNonNull(event.getGuild()), Objects.requireNonNull(event.getMember()));
			}
			else{
				result = command.executeUser(event);
			}
			
			switch(result){
				case FAILED -> JDAWrappers.edit(event, "Failed to execute command " + event.getCommandPath()).submitAndDelete(5);
				case BAD_ARGUMENTS -> JDAWrappers.edit(event, "Bad arguments").submitAndDelete(5);
				case NOT_ALLOWED -> JDAWrappers.edit(event, "You're not allowed to use this command").submitAndDelete(5);
				case HANDLED_NO_MESSAGE -> JDAWrappers.edit(event, "OK").submitAndDelete(5);
				case NOT_IMPLEMENTED -> JDAWrappers.edit(event, "Command cannot be used in this channel").submitAndDelete(5);
			}
		}
		catch(Exception e){
			log.error("Failed to execute command {}", command, e);
			JDAWrappers.edit(event, "Error executing command (%s)".formatted(e.getClass().getName())).submitAndDelete(5);
		}
	}
	
	private void performAutoComplete(@NotNull CommandAutoCompleteInteractionEvent event, @NotNull IExecutableCommand<?> command){
		try{
			if(event.isFromGuild()){
				command.autoCompleteGuild(event, Objects.requireNonNull(event.getGuild()), Objects.requireNonNull(event.getMember()));
			}
			else{
				command.autoCompleteUser(event);
			}
		}
		catch(Exception e){
			log.error("Failed to execute command {}", command, e);
		}
	}
	
	private boolean checkPermission(@NotNull CommandInteraction interaction, @NotNull IExecutableCommand<?> command){
		if(!interaction.isFromGuild()){
			return true;
		}
		
		if(command.getPermission().isGuildAllowed(interaction.getCommandPath(), interaction.getMember())
		   || command.isSpecificAllowed(interaction.getMember())){
			return true;
		}
		
		JDAWrappers.edit(interaction, "You're not allowed to use this command").submitAndDelete(5);
		return false;
	}
}
