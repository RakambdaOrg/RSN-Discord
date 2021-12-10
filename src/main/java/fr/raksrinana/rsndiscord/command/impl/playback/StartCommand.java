package fr.raksrinana.rsndiscord.command.impl.playback;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED_NO_MESSAGE;

@Log4j2
public class StartCommand extends SubCommand{
	@Override
	@NotNull
	public String getId(){
		return "start";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Start playback";
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandEvent event, @NotNull Guild guild, @NotNull Member member){
		var voiceState = member.getVoiceState();
		
		if(!voiceState.inAudioChannel()){
			JDAWrappers.edit(event, "You're not in a voice channel...").submit();
			return HANDLED;
		}
		
		var audioManager = guild.getAudioManager();
		var echoHandler = new EchoHandler();
		
		audioManager.setSendingHandler(echoHandler);
		audioManager.setReceivingHandler(echoHandler);
		audioManager.openAudioConnection(voiceState.getChannel());
		
		return HANDLED_NO_MESSAGE;
	}
}
