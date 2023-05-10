package fr.rakambda.rsndiscord.spring.interaction.slash.impl.music;

import fr.rakambda.rsndiscord.spring.audio.AudioServiceFactory;
import fr.rakambda.rsndiscord.spring.audio.scheduler.TrackUserDataFields;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import static java.awt.Color.CYAN;
import static java.awt.Color.RED;

@Component
public class NowPlayingCommand implements IExecutableSlashCommandGuild{
	private final AudioServiceFactory audioServiceFactory;
	private final LocalizationService localizationService;
	
	@Autowired
	public NowPlayingCommand(AudioServiceFactory audioServiceFactory, LocalizationService localizationService){
		this.audioServiceFactory = audioServiceFactory;
		this.localizationService = localizationService;
	}
	
	@Override
	@NotNull
	public String getId(){
		return "playing";
	}
	
	@Override
	@NotNull
	public String getPath(){
		return "music/playing";
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var deferred = event.deferReply().submit();
		
		var currentTrackOptional = audioServiceFactory.get(guild).getTrackScheduler().getCurrentTrack();
		var locale = event.getUserLocale();
		
		if(currentTrackOptional.isEmpty()){
			var embed = new EmbedBuilder().setColor(RED)
					.setDescription(localizationService.translate(locale, "music.nothing-playing"))
					.build();
			return deferred.thenCompose(empty -> JDAWrappers.edit(event, embed).submit());
		}
		
		var currentTrack = currentTrackOptional.get();
		var userData = currentTrack.getUserData(TrackUserDataFields.class);
		
		var requester = Optional.ofNullable(userData)
				.map(TrackUserDataFields::getRequesterId)
				.map(User::fromId)
				.map(IMentionable::getAsMention)
				.orElseGet(() -> localizationService.translate(locale, "music.unknown-requester"));
		
		var repeating = Optional.ofNullable(userData)
				.map(TrackUserDataFields::isRepeat)
				.orElse(Boolean.FALSE)
				.toString();
		
		var progressBar = String.format("%s %s / %s",
				NowPlayingCommand.buildBar(currentTrack.getPosition(), currentTrack.getDuration()),
				getDuration(currentTrack.getPosition()),
				getDuration(currentTrack.getDuration()));
		
		var embed = new EmbedBuilder().setColor(CYAN)
				.setTitle(localizationService.translate(locale, "music.currently-playing"), currentTrack.getInfo().uri)
				.setDescription(currentTrack.getInfo().title)
				.addField(localizationService.translate(locale, "music.requester"), requester, true)
				.addField(localizationService.translate(locale, "music.repeating"), repeating, true)
				.addField(localizationService.translate(locale, "music.position"), progressBar, true)
				.build();
		
		return deferred.thenCompose(empty -> JDAWrappers.edit(event, embed).submit());
	}
	
	@NotNull
	private static String buildBar(double current, double total){
		var charCount = 10;
		if(current > total){
			current = total;
		}
		var sb = new StringBuilder("=".repeat(charCount));
		var replaceIndex = (int) ((current / total) * charCount);
		sb.replace(replaceIndex, replaceIndex + 1, "O");
		return sb.toString();
	}
	
	/**
	 * Get the duration in a readable format.
	 *
	 * @param time The duration in milliseconds.
	 *
	 * @return A readable version of this duration.
	 */
	@NotNull
	public static String getDuration(long time){
		var duration = Duration.ofMillis(time);
		if(duration.toHoursPart() > 0){
			return String.format("%02d:%02d:%02d", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
		}
		return String.format("%02d:%02d", duration.toMinutesPart(), duration.toSecondsPart());
	}
}
