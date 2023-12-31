package fr.rakambda.rsndiscord.spring.scheduledevents;

import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ScheduledEventsService{
	private static final Collection<ScheduledEvent.Status> WANTED_STATUS = Set.of(ScheduledEvent.Status.SCHEDULED, ScheduledEvent.Status.ACTIVE);
	
	private final ChannelRepository channelRepository;
	
	public ScheduledEventsService(ChannelRepository channelRepository){
		this.channelRepository = channelRepository;
	}
	
	public void update(@NotNull Guild guild){
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
		
		channels.stream()
				.map(entity -> JDAWrappers.findChannel(guild.getJDA(), entity.getChannelId()))
				.flatMap(Optional::stream)
				.forEach(c -> clearChannel(c).thenCompose(empty -> JDAWrappers.message(c, messageContent).submit()));
	}
	
	@NotNull
	private String getEventText(@NotNull ScheduledEvent event){
		StringBuilder stringBuilder = new StringBuilder();
		
		if(event.getStatus() == ScheduledEvent.Status.ACTIVE){
			stringBuilder.append("🔴 NOW");
		}
		else{
			stringBuilder.append(TimeFormat.RELATIVE.format(event.getStartTime().toEpochSecond() * 1000));
		}
		
		if(Objects.nonNull(event.getEndTime())){
			stringBuilder.append(" (ending ")
					.append(TimeFormat.RELATIVE.format(event.getEndTime().toEpochSecond() * 1000))
					.append(")");
		}
		
		stringBuilder.append(": **")
				.append(event.getName())
				.append("**");
		
		if(Objects.nonNull(event.getDescription())){
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
