package fr.rakambda.rsndiscord.spring.amqp.listeners;

import fr.rakambda.rsndiscord.spring.amqp.QuartzService;
import fr.rakambda.rsndiscord.spring.amqp.message.DeleteMessageDelayMessage;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class DelayedMessageHandler implements Job{
	private static final Duration RETRY_DELAY = Duration.ofMinutes(15);
	
	private final JDA jda;
	private final JsonMapper jsonMapper;
	private final QuartzService quartzService;
	
	public DelayedMessageHandler(JDA jda, JsonMapper jsonMapper, QuartzService quartzService){
		this.jda = jda;
		this.jsonMapper = jsonMapper;
		this.quartzService = quartzService;
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException{
		var payload = context.get("payload");
		var data = jsonMapper.convertValue(payload, DeleteMessageDelayMessage.class);
		handleDeleteMessage(data).exceptionally(e -> handleError(e, context, data));
	}
	
	@Nullable
	private <T> T handleError(@NonNull Throwable throwable, @NonNull JobExecutionContext context, @NonNull DeleteMessageDelayMessage message) {
		log.warn("Failed to handle message", throwable);
		
		var retry = Optional.ofNullable(context.get("retry"))
				.filter(Integer.class::isInstance)
				.map(Integer.class::cast)
				.orElse(0);
		if(retry <= 0){
			log.warn("Retry count for message is {}, not retrying", retry);
			return null;
		}
		
		quartzService.scheduleMessage(RETRY_DELAY, retry - 1, message);
		return null;
	}
	
	@NonNull
	private CompletableFuture<?> handleDeleteMessage(@NonNull DeleteMessageDelayMessage message){
		log.info("New delay message received {}", message);
		
		var channel = jda.getTextChannelById(message.getChannelId());
		if(Objects.isNull(channel)){
			return CompletableFuture.failedFuture(new IllegalStateException("Failed to find channel " + message.getChannelId()));
		}
		return channel.retrieveMessageById(message.getMessageId()).submit()
				.thenCompose(this::deleteThread)
				.thenCompose(m -> JDAWrappers.delete(m).submit());
	}
	
	@NonNull
	private CompletableFuture<Message> deleteThread(@NonNull Message m){
		return Optional.ofNullable(m.getStartedThread())
				.map(t -> JDAWrappers.delete(t).submit().thenApply(_ -> m))
				.orElseGet(() -> CompletableFuture.completedFuture(m));
	}
}
