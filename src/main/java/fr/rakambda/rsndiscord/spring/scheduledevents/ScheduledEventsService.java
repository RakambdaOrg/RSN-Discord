package fr.rakambda.rsndiscord.spring.scheduledevents;

import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.log.LogContext;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ScheduledEventsService{
	private static final Collection<ScheduledEvent.Status> WANTED_STATUS = Set.of(ScheduledEvent.Status.SCHEDULED, ScheduledEvent.Status.ACTIVE);
	
	private final ChannelRepository channelRepository;
	private final LocalizationService localizationService;
	
	public ScheduledEventsService(ChannelRepository channelRepository, LocalizationService localizationService){
		this.channelRepository = channelRepository;
		this.localizationService = localizationService;
	}
	
	public void update(@NotNull Guild guild){
		try(var ignored = LogContext.with(guild)){
			var channels = channelRepository.findAllByGuildIdAndType(guild.getIdLong(), ChannelType.SCHEDULED_EVENTS);
			if(channels.isEmpty()){
				return;
			}
			
			var events = guild.getScheduledEvents();
			var messageContent = events.stream()
					.filter(e -> WANTED_STATUS.contains(e.getStatus()))
					.sorted()
					.map(this::getEventText)
					.map("* %s"::formatted)
					.collect(Collectors.joining("\n"));
			
			log.info("Updating scheduled events for guild {}", guild);
			
			channels.stream()
					.map(entity -> JDAWrappers.findChannel(guild.getJDA(), entity.getChannelId()))
					.flatMap(Optional::stream)
					.forEach(c -> clearChannel(c).thenCompose(empty -> JDAWrappers.message(c, messageContent).submit()));
		}
	}
	
	@NotNull
	private String getEventText(@NotNull ScheduledEvent event){
		StringBuilder stringBuilder = new StringBuilder();
		
		if(event.getStatus() == ScheduledEvent.Status.ACTIVE){
			stringBuilder.append("🔴 ")
					.append(localizationService.translate(event.getGuild().getLocale(), "scheduled.event.live"));
		}
		else{
			stringBuilder.append(TimeFormat.DATE_TIME_LONG.format(event.getStartTime().toEpochSecond() * 1000));
		}
		
		stringBuilder.append(": **")
				.append(event.getName())
				.append("**");
		
		if(StringUtils.hasText(event.getDescription())){
			stringBuilder.append(" - ")
					.append(event.getDescription());
		}
		
		return stringBuilder.toString();
	}
	
	@NotNull
	private CompletableFuture<Void> clearChannel(@NotNull GuildMessageChannel channel){
		return JDAWrappers.history(channel)
				.takeAsync(1000)
				.thenCompose(this::deleteAll);
	}
	
	@NotNull
	private CompletableFuture<Void> deleteAll(@NotNull Collection<Message> messages){
		return messages.stream()
				.map(this::processMessage)
				.reduce((left, right) -> left.thenCombine(right, (v1, v2) -> null))
				.orElseGet(() -> CompletableFuture.completedFuture(null));
	}
	
	@NotNull
	private CompletableFuture<Void> processMessage(@NotNull Message message){
		var thread = message.getStartedThread();
		if(Objects.isNull(thread)){
			return JDAWrappers.delete(message).submit();
		}
		
		return JDAWrappers.delete(thread).submit().thenCompose(empty -> JDAWrappers.delete(message).submit());
	}
}
