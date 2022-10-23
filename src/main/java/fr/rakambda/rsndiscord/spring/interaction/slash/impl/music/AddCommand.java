package fr.rakambda.rsndiscord.spring.interaction.slash.impl.music;

import fr.rakambda.rsndiscord.spring.audio.AudioServiceFactory;
import fr.rakambda.rsndiscord.spring.audio.exception.LoadFailureException;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import static java.util.Optional.ofNullable;

@Component
public class AddCommand implements IExecutableSlashCommandGuild{
	public static final String QUERY_OPTION_ID = "query";
	public static final String SKIP_OPTION_ID = "skip";
	public static final String MAX_OPTION_ID = "max";
	public static final String REPEAT_OPTION_ID = "repeat";
	
	private final AudioServiceFactory audioServiceFactory;
	private final LocalizationService localizationService;
	
	@Autowired
	public AddCommand(AudioServiceFactory audioServiceFactory, LocalizationService localizationService){
		this.audioServiceFactory = audioServiceFactory;
		this.localizationService = localizationService;
	}
	
	@Override
	@NotNull
	public String getId(){
		return "add";
	}
	
	@Override
	@NotNull
	public String getPath(){
		return "music/add";
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member) throws LoadFailureException{
		var deferred = event.deferReply().submit();
		var user = member.getUser();
		
		var voiceChannel = ofNullable(event.getMember())
				.map(Member::getVoiceState)
				.map(GuildVoiceState::getChannel)
				.orElseThrow(() -> new IllegalStateException("You must be in a voice channel"));
		
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
		
		var audioService = audioServiceFactory.get(guild);
		var trackConsumer = new AddMusicITrackLoadListener(audioService.getTrackScheduler(), event, localizationService);
		
		audioService.join(voiceChannel);
		audioService.play(user, identifier, repeat, skipCount, maxTracks, trackConsumer);
		return deferred;
	}
}
