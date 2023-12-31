package fr.rakambda.rsndiscord.spring.interaction.slash.impl;

import fr.rakambda.rsndiscord.spring.interaction.slash.api.IRegistrableSlashCommand;
import fr.rakambda.rsndiscord.spring.interaction.slash.impl.music.AddCommand;
import fr.rakambda.rsndiscord.spring.interaction.slash.impl.music.QueueCommand;
import fr.rakambda.rsndiscord.spring.interaction.slash.impl.music.SeekCommand;
import fr.rakambda.rsndiscord.spring.interaction.slash.impl.music.VolumeCommand;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import static net.dv8tion.jda.api.interactions.commands.OptionType.BOOLEAN;
import static net.dv8tion.jda.api.interactions.commands.OptionType.INTEGER;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@Configuration
public class MusicGroupCommand implements IRegistrableSlashCommand{
	@Override
	@NotNull
	public CommandData getDefinition(@NotNull LocalizationFunction localizationFunction){
		return Commands.slash(getId(), "Music commands")
				.setLocalizationFunction(localizationFunction)
				.setGuildOnly(true)
				.addSubcommands(
						new SubcommandData("add", "Add music")
								.addOptions(
										new OptionData(STRING, AddCommand.QUERY_OPTION_ID, "Track or query to add")
												.setRequired(true),
										new OptionData(INTEGER, AddCommand.SKIP_OPTION_ID, "The number of items to skip if this is a playlist")
												.setRequired(false),
										new OptionData(INTEGER, AddCommand.MAX_OPTION_ID, "The maximum number of tracks to import from a playlist")
												.setRequired(false),
										new OptionData(BOOLEAN, AddCommand.REPEAT_OPTION_ID, "If the tracks should be repeated in the queue")
												.setRequired(false)
								),
						new SubcommandData("playing", "Display the current track"),
						new SubcommandData("pause", "Pause playback"),
						new SubcommandData("queue", "Displays the queue")
								.addOptions(
										new OptionData(INTEGER, QueueCommand.PAGE_OPTION_ID, "Page to get")),
						new SubcommandData("resume", "Resume playback"),
						new SubcommandData("seek", "Seek time in the track")
								.addOptions(
										new OptionData(STRING, SeekCommand.TIME_OPTION_ID, "Time to seek")
												.setRequired(true)
								),
						new SubcommandData("skip", "Skip the current music"),
						new SubcommandData("stop", "Stop the music"),
						new SubcommandData("volume", "Set the volume bot")
								.addOptions(
										new OptionData(INTEGER, VolumeCommand.VOLUME_OPTION_ID, "The volume to set")
												.setRequired(true)
								)
				);
	}
	
	@Override
	@NotNull
	public String getId(){
		return "music";
	}
}
