package fr.raksrinana.rsndiscord.command2;

import fr.raksrinana.rsndiscord.command2.api.IExecutableCommand;
import fr.raksrinana.rsndiscord.event.EventListener;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessageMins;

@EventListener
public class SlashCommandListener extends ListenerAdapter{
	@Override
	public void onSlashCommand(@NotNull SlashCommandEvent event){
		super.onSlashCommand(event);
		
		if(event.isFromGuild()){
			Log.getLogger(event.getGuild()).info("Received slash-command {} from {} with args {}", event.getCommandPath(), event.getUser(), getArgsForLogs(event.getOptions()));
			
			SlashCommandService.getExecutableCommand(event.getCommandPath()).ifPresentOrElse(
					command -> performCommand(event, command),
					() -> event.reply("Unknown command " + event.getCommandPath()).setEphemeral(true).submit());
		}
	}
	
	@NotNull
	private String getArgsForLogs(@NotNull List<OptionMapping> options){
		return options.stream()
				.map(option -> "%s(%s)[%s]".formatted(option.getName(), option.getType(), option.getAsString()))
				.collect(Collectors.joining(", "));
	}
	
	private void performCommand(@NotNull SlashCommandEvent event, @NotNull IExecutableCommand command){
		event.deferReply(command.replyEphemeral()).submit();
		
		try{
			if(command.getPermission().isAllowed(event.getCommandPath(), event.getMember())){
				switch(command.execute(event)){
					case FAILED -> JDAWrappers.replyCommand(event, "Failed to execute command " + event.getCommandPath()).submit().thenAccept(deleteMessageMins(5));
					case BAD_ARGUMENTS -> JDAWrappers.replyCommand(event, "Bad arguments").submit().thenAccept(deleteMessageMins(5));
					case NOT_ALLOWED -> JDAWrappers.replyCommand(event, "You're not allowed to use this command").submit().thenAccept(deleteMessageMins(5));
				}
			}
			else{
				JDAWrappers.replyCommand(event, "You're not allowed to use this command").submit().thenAccept(deleteMessageMins(5));
			}
		}
		catch(Exception e){
			Log.getLogger(event.getGuild()).error("Failed to execute command {}", command, e);
			JDAWrappers.replyCommand(event, "Error executing command (%s)".formatted(e.getClass().getName())).submit().thenAccept(deleteMessageMins(5));
		}
	}
}
