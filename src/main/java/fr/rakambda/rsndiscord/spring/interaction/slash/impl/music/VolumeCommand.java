package fr.rakambda.rsndiscord.spring.interaction.slash.impl.music;

import fr.rakambda.rsndiscord.spring.audio.AudioServiceFactory;
import fr.rakambda.rsndiscord.spring.interaction.exception.InvalidStateException;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.storage.repository.AudioRepository;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
public class VolumeCommand implements IExecutableSlashCommandGuild{
	public static final String VOLUME_OPTION_ID = "volume";
	
	private final AudioServiceFactory audioServiceFactory;
	private final AudioRepository audioRepository;
	private final LocalizationService localizationService;
	
	@Autowired
	public VolumeCommand(AudioServiceFactory audioServiceFactory, AudioRepository audioRepository, LocalizationService localizationService){
		this.audioServiceFactory = audioServiceFactory;
		this.audioRepository = audioRepository;
		this.localizationService = localizationService;
	}
	
	@Override
	@NonNull
	public String getId(){
		return "volume";
	}
	
	@Override
	@NonNull
	public String getPath(){
		return "music/volume";
	}
	
	@Override
	@NonNull
	public CompletableFuture<?> executeGuild(@NonNull SlashCommandInteraction event, @NonNull Guild guild, @NonNull Member member) throws InvalidStateException{
		var deferred = event.deferReply().submit();
		
		var requestedVolume = getOptionAsInt(event.getOption(VOLUME_OPTION_ID)).orElseThrow();
		var volume = Math.min(100, Math.max(0, requestedVolume));
		
		audioServiceFactory.get(guild).getTrackScheduler().setVolume(volume);
		
		var guildEntity = audioRepository.findByGuildId(guild.getIdLong()).orElseThrow(() -> new InvalidStateException("Failed to save settings for guild"));
		guildEntity.setVolume(volume);
		audioRepository.save(guildEntity);
		
		var content = localizationService.translate(event.getUserLocale(), "music.volume-set", volume);
		return deferred.thenCompose(empty -> JDAWrappers.edit(event, content).submit());
	}
}
