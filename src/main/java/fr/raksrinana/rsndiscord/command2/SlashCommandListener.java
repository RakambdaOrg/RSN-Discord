package fr.raksrinana.rsndiscord.command2;

import fr.raksrinana.rsndiscord.command2.api.IExecutableCommand;
import fr.raksrinana.rsndiscord.event.EventListener;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;

@EventListener
public class SlashCommandListener extends ListenerAdapter{
	@Override
	public void onSlashCommand(@NotNull SlashCommandEvent event){
		super.onSlashCommand(event);
		
		if(event.isFromGuild()){
			Log.getLogger(event.getGuild()).info("Received slash-command {} from {}", event.getCommandPath(), event.getUser());
			
			SlashCommandService.getExecutableCommand(event.getCommandPath()).ifPresentOrElse(
					command -> performCommand(event, command),
					() -> event.reply("Unknown command " + event.getCommandPath()).setEphemeral(true).submit());
		}
	}
	
	private void performCommand(SlashCommandEvent event, IExecutableCommand command){
		event.deferReply(command.replyEphemeral()).submit();
		
		try{
			switch(command.execute(event)){
				case FAILED -> JDAWrappers.replyCommand(event, "Failed to execute command " + event.getCommandPath()).submit();
				case BAD_ARGUMENTS -> JDAWrappers.replyCommand(event, "Bad arguments").submit();
				case NOT_ALLOWED -> JDAWrappers.replyCommand(event, "You're not allowed to use this command").submit();
			}
		}
		catch(Exception e){
			Log.getLogger(event.getGuild()).error("Failed to execute command {}", command, e);
			JDAWrappers.replyCommand(event, "Error executing command (%s)".formatted(e.getClass().getName())).submit().thenAccept(deleteMessage(date -> date.plusMinutes(5)));
		}
	}
}
