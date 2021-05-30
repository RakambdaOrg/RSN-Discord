package fr.raksrinana.rsndiscord.command2.impl.music;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Optional.ofNullable;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class AddCommand extends SubCommand{
	private static final String QUERY_OPTION_ID = "query";
	private static final String SKIP_OPTION_ID = "skip";
	private static final String MAX_OPTION_ID = "max";
	private static final String REPEAT_OPTION_ID = "repeat";
	
	@Override
	@NotNull
	public String getId(){
		return "add";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Add a music";
	}
	
	@Override
	public boolean replyEphemeral(){
		return false;
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(
				new OptionData(STRING, QUERY_OPTION_ID, "Query to add (link)").setRequired(true),
				new OptionData(INTEGER, SKIP_OPTION_ID, "The number of items to skip if this is a playlist").setRequired(false),
				new OptionData(STRING, MAX_OPTION_ID, "The maximum number of tracks to import from a playlist").setRequired(false),
				new OptionData(BOOLEAN, REPEAT_OPTION_ID, "If the tracks should be repeated in the queue").setRequired(false)
		);
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		var member = event.getMember();
		var user = member.getUser();
		
		var voiceChannel = ofNullable(event.getMember())
				.map(Member::getVoiceState)
				.map(GuildVoiceState::getChannel);
		
		if(voiceChannel.isEmpty()){
			JDAWrappers.replyCommand(event, translate(guild, "music.voice-error")).submitAndDelete(5);
			return SUCCESS;
		}
		
		var identifier = event.getOption(QUERY_OPTION_ID).getAsString().trim();
		var skipCount = getOptionAsInt(event.getOption(SKIP_OPTION_ID))
				.filter(value -> value >= 0)
				.orElse(0);
		var maxTracks = getOptionAsInt(event.getOption(MAX_OPTION_ID))
				.filter(value -> value >= 0)
				.orElse(10);
		var repeat = ofNullable(event.getOption(REPEAT_OPTION_ID))
				.map(OptionMapping::getAsBoolean)
				.orElse(false);
		
		var trackConsumer = new AddMusicTrackConsumer(guild, event, user, repeat);
		RSNAudioManager.play(user, voiceChannel.get(), trackConsumer, skipCount, maxTracks, identifier);
		return SUCCESS;
	}
}
