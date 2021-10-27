package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.SimpleCommand;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;

// @BotSlashCommand
@Log4j2
public class VoiceActivityCommand extends SimpleCommand{
	private static final String APPLICATION_OPTION_ID = "application";
	private static final String ANNOUNCE_CHANNEL_OPTION_ID = "announce-channel";
	private static final String VOICE_CHANNEL_OPTION_ID = "voice-channel";
	
	@Override
	@NotNull
	public String getId(){
		return "voice-activity";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Starts a discord voice application";
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(
				new OptionData(OptionType.STRING, APPLICATION_OPTION_ID, "Application to start")
						.addChoice("Youtube Together", "755600276941176913")
						.addChoice("Youtube Together Dev", "880218832743055411")
						.addChoice("Betrayal.io", "773336526917861400")
						.addChoice("Fishington.io", "814288819477020702")
						.addChoice("Chess In The Park", "832012774040141894")
						.addChoice("CG 2 Dev", "832012586023256104")
						.addChoice("Awkword", "879863881349087252")
						.addChoice("Spellcast", "852509694341283871")
						.addChoice("Doodlecrew", "878067389634314250")
						.addChoice("Wordsnack", "879863976006127627")
						.addChoice("Lettertile", "879863686565621790")
						.setRequired(true),
				new OptionData(OptionType.CHANNEL, VOICE_CHANNEL_OPTION_ID, "Channel to start the activity in")
						.setChannelTypes(ChannelType.VOICE)
						.setRequired(true),
				new OptionData(OptionType.CHANNEL, ANNOUNCE_CHANNEL_OPTION_ID, "Channel to send the invite in")
						.setChannelTypes(ChannelType.TEXT)
						.setRequired(true));
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var applicationId = event.getOption(APPLICATION_OPTION_ID).getAsString();
		var voiceChannel = event.getOption(VOICE_CHANNEL_OPTION_ID).getAsGuildChannel();
		var announceChannel = event.getOption(ANNOUNCE_CHANNEL_OPTION_ID).getAsGuildChannel();
		
		if(!(voiceChannel instanceof AudioChannel voiceApplicationChannel)){
			log.error("Application channel cannot be used {}", voiceChannel);
			return CommandResult.FAILED;
		}
		
		if(!(announceChannel instanceof TextChannel textAnnounceChannel)){
			log.error("Announce channel cannot be used {}", announceChannel);
			return CommandResult.FAILED;
		}
		
		// voiceApplicationChannel.createInvite()
		// 		.setTargetApplication(applicationId)
		// 		.submit()
		// 		.thenAccept(invite -> {
		// 			JDAWrappers.edit(event, "Invitation sent").submit();
		// 			JDAWrappers.message(textAnnounceChannel, invite.getUrl()).submit();
		// 		});
		return HANDLED;
	}
}
