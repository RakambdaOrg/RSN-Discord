package fr.rakambda.rsndiscord.spring.interaction.slash.impl.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.rakambda.rsndiscord.spring.audio.exception.TrackLoadException;
import fr.rakambda.rsndiscord.spring.audio.load.ITrackLoadListener;
import fr.rakambda.rsndiscord.spring.audio.scheduler.ITrackScheduler;
import fr.rakambda.rsndiscord.spring.audio.scheduler.TrackUserDataFields;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jspecify.annotations.NonNull;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import static fr.rakambda.rsndiscord.spring.interaction.slash.impl.music.NowPlayingCommand.getDuration;
import static java.awt.Color.GREEN;

@Slf4j
@RequiredArgsConstructor
public class AddMusicITrackLoadListener implements ITrackLoadListener{
	private final ITrackScheduler scheduler;
	private final SlashCommandInteraction event;
	private final LocalizationService localizationService;
	
	@Override
	public void onTrackLoaded(@NonNull AudioTrack track){
		var queue = scheduler.getQueue();
		
		var before = queue.stream()
				.takeWhile(t -> !Objects.equals(track, t))
				.toList();
		
		var isCurrentTrack = scheduler.getCurrentTrack()
				.map(trk -> Objects.equals(trk, track))
				.orElse(false);
		
		var currentTrackTimeLeft = scheduler.getCurrentTrack()
				.map(t -> t.getDuration() - t.getPosition())
				.filter(e -> !queue.isEmpty())
				.orElse(0L);
		var queueTime = before.stream()
				.mapToLong(AudioTrack::getDuration)
				.sum();
		var delay = currentTrackTimeLeft + queueTime;
		
		var author = event.getUser();
		var locale = event.getUserLocale();
		var repeat = Optional.ofNullable(track.getUserData(TrackUserDataFields.class)).map(TrackUserDataFields::isRepeat).orElse(false);
		
		var builder = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
				.setColor(GREEN)
				.setTitle(localizationService.translate(locale, "music.track.added"), track.getInfo().uri)
				.setDescription(track.getInfo().title)
				.addField(localizationService.translate(locale, "music.requester"), author.getAsMention(), true)
				.addField(localizationService.translate(locale, "music.track.duration"), getDuration(track.getDuration()), true)
				.addField(localizationService.translate(locale, "music.track.eta"), getDuration(delay), true)
				.addField(localizationService.translate(locale, "music.repeating"), Boolean.toString(repeat), true);
		if(!isCurrentTrack){
			builder.addField(localizationService.translate(locale, "music.queue.position"), String.valueOf(1 + before.size()), true);
		}
		JDAWrappers.reply(event, builder.build()).submit();
	}
	
	@Override
	public void onPlaylistLoaded(@NonNull Collection<AudioTrack> tracks){
	}
	
	@Override
	public void onLoadFailure(@NonNull TrackLoadException throwable){
		log.error("Failed to load music", throwable);
		JDAWrappers.reply(event, throwable.getMessage()).submit(); //TODO friendly message
	}
}
