package fr.raksrinana.rsndiscord.command.impl.playback;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED_NO_MESSAGE;

@Log4j2
public class StopCommand extends SubCommand{
	@Override
	@NotNull
	public String getId(){
		return "stop";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Stop playback";
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var audioManager = event.getGuild().getAudioManager();
		if(audioManager.isConnected()){
			audioManager.closeAudioConnection();
			audioManager.setSendingHandler(null);
			audioManager.setReceivingHandler(null);
		}
		
		return HANDLED_NO_MESSAGE;
	}
}
