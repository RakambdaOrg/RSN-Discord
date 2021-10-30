package fr.raksrinana.rsndiscord.command;

import fr.raksrinana.rsndiscord.command.api.IExecutableCommand;
import fr.raksrinana.rsndiscord.event.EventListener;
import fr.raksrinana.rsndiscord.log.LogContext;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@EventListener
@Log4j2
public class SlashCommandListener extends ListenerAdapter{
	@Override
	public void onSlashCommand(@NotNull SlashCommandEvent event){
		super.onSlashCommand(event);
		
		try(var ignored = LogContext.with(event.getGuild()).with(event.getUser())){
			log.info("Received slash-command {} from {} with args {}", event.getCommandPath(), event.getUser(), getArgsForLogs(event.getOptions()));
			
			SlashCommandService.getExecutableCommand(event.getCommandPath()).ifPresentOrElse(
					command -> event.deferReply(command.replyEphemeral()).submit().thenAccept(empty -> performCommand(event, command)),
					() -> event.reply("Unknown command {%s".formatted(event.getCommandPath())).setEphemeral(true).submit());
		}
	}
	
	@NotNull
	private String getArgsForLogs(@NotNull List<OptionMapping> options){
		return options.stream()
				.map(option -> "%s(%s)[%s]".formatted(option.getName(), option.getType(), option.getAsString()))
				.collect(Collectors.joining(", "));
	}
	
	private void performCommand(@NotNull SlashCommandEvent event, @NotNull IExecutableCommand command){
		try{
			boolean allowed;
			if(event.isFromGuild()){
				allowed = command.getPermission().isGuildAllowed(event.getCommandPath(), event.getMember())
				          && command.isSpecificAllowed(event.getMember());
			}
			else{
				allowed = true;
			}
			if(!allowed){
				JDAWrappers.edit(event, "You're not allowed to use this command").submitAndDelete(5);
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
}
