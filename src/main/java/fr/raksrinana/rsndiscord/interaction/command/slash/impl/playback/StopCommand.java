package fr.raksrinana.rsndiscord.interaction.command.slash.impl.playback;

import fr.raksrinana.rsndiscord.interaction.command.CommandResult;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubSlashCommand;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.interaction.command.CommandResult.HANDLED_NO_MESSAGE;

@Log4j2
public class StopCommand extends SubSlashCommand{
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
	public CommandResult executeGuild(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		var audioManager = guild.getAudioManager();
		if(audioManager.isConnected()){
			audioManager.closeAudioConnection();
			audioManager.setSendingHandler(null);
			audioManager.setReceivingHandler(null);
		}
		
		return HANDLED_NO_MESSAGE;
	}
}
