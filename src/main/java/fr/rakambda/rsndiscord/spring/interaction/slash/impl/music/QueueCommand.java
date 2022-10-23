package fr.rakambda.rsndiscord.spring.interaction.slash.impl.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import static java.awt.Color.PINK;

@Component
public class QueueCommand implements IExecutableSlashCommandGuild{
	public static final String PAGE_OPTION_ID = "page";
	
	private final AudioServiceFactory audioServiceFactory;
	private final LocalizationService localizationService;
	
	@Autowired
	public QueueCommand(AudioServiceFactory audioServiceFactory, LocalizationService localizationService){
		this.audioServiceFactory = audioServiceFactory;
		this.localizationService = localizationService;
	}
	
	@Override
	@NotNull
	public String getId(){
		return "queue";
	}
	
	@Override
	@NotNull
	public String getPath(){
		return "music/queue";
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		var deferred = event.deferReply().submit();
		
		var author = event.getUser();
		var trackScheduler = audioServiceFactory.get(guild).getTrackScheduler();
		var queue = trackScheduler.getQueue();
		var perPage = 10L;
		var maxPageNumber = (int) Math.ceil(queue.size() / (double) perPage);
		
		long page = getOptionAsInt(event.getOption(PAGE_OPTION_ID))
				.map(val -> val - 1L)
				.orElse(0L);
		var position = new AtomicLong(perPage * page);
		
		var currentTrackTimeLeft = trackScheduler.getCurrentTrack()
				.map(t -> t.getDuration() - t.getPosition())
				.orElse(0L);
		var queueDuration = queue.stream().limit(perPage * page)
				.mapToLong(AudioTrack::getDuration)
				.sum();
		var beforeDuration = new AtomicLong(currentTrackTimeLeft + queueDuration);
		
		var locale = event.getUserLocale();
		var builder = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
				.setColor(PINK)
				.setTitle(localizationService.translate(locale, "music.queue.page", page + 1, maxPageNumber))
				.setDescription(localizationService.translate(locale, "music.queue.size", queue.size()));
		
		queue.stream().skip(perPage * page)
				.limit(perPage)
				.forEachOrdered(track -> {
					var userData = Optional.ofNullable(track.getUserData(TrackUserDataFields.class));
					var requester = userData.map(TrackUserDataFields::getRequesterId)
							.map(User::fromId)
							.map(IMentionable::getAsMention)
							.orElseGet(() -> localizationService.translate(locale, "music.unknown-requester"));
					var repeating = userData.map(TrackUserDataFields::isRepeat)
							.orElse(Boolean.FALSE)
							.toString();
					
					var trackInfo = track.getInfo().title +
					                "\n" + localizationService.translate(locale, "music.requester") + ": " + requester +
					                "\n" + localizationService.translate(locale, "music.repeating") + ": " + repeating +
					                "\n" + localizationService.translate(locale, "music.track.duration") + ": " + NowPlayingCommand.getDuration(track.getDuration()) +
					                "\n" + localizationService.translate(locale, "music.track.eta") + ": " + NowPlayingCommand.getDuration(beforeDuration.get());
					
					builder.addField("Position " + position.addAndGet(1), trackInfo, false);
					beforeDuration.addAndGet(track.getDuration());
				});
		
		return deferred.thenCompose(empty -> JDAWrappers.edit(event, builder.build()).submit());
	}
}
