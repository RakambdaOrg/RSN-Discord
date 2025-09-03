package fr.rakambda.rsndiscord.spring.interaction.slash.impl.music;

import fr.rakambda.rsndiscord.spring.amqp.RabbitService;
import fr.rakambda.rsndiscord.spring.audio.AudioServiceFactory;
import fr.rakambda.rsndiscord.spring.audio.exception.NoTrackPlayingException;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class SkipCommand implements IExecutableSlashCommandGuild{
	private final AudioServiceFactory audioServiceFactory;
	private final LocalizationService localizationService;
	private final RabbitService rabbitService;
	
	@Autowired
	public SkipCommand(AudioServiceFactory audioServiceFactory, LocalizationService localizationService, RabbitService rabbitService){
		this.audioServiceFactory = audioServiceFactory;
		this.localizationService = localizationService;
		this.rabbitService = rabbitService;
	}
	
	@Override
	@NonNull
	public String getId(){
		return "skip";
	}
	
	@Override
	@NonNull
	public String getPath(){
		return "music/skip";
	}
	
	@Override
	@NonNull
	public CompletableFuture<?> executeGuild(@NonNull SlashCommandInteraction event, @NonNull Guild guild, @NonNull Member member) throws NoTrackPlayingException{
		var deferred = event.deferReply().submit();
		
		var trackScheduler = audioServiceFactory.get(guild).getTrackScheduler();
		var track = trackScheduler.getCurrentTrack().orElseThrow(() -> new IllegalStateException("Nothing playing"));
		var locale = event.getUserLocale();
		
		if(track.getDuration() - track.getPosition() < 30000){
			return deferred.thenCompose(empty -> JDAWrappers.edit(event, localizationService.translate(locale, "music.skip.soon-finish")).submit());
		}
		
		trackScheduler.skip();
		return deferred.thenCompose(empty -> JDAWrappers.edit(event, localizationService.translate(locale, "music.skipped", event.getUser().getAsMention()))
				.submitAndDelete(5, rabbitService));
	}
}
