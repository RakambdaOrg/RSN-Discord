package fr.rakambda.rsndiscord.spring.amqp.listeners;

import fr.rakambda.rsndiscord.spring.amqp.AmqpNameProvider;
import fr.rakambda.rsndiscord.spring.amqp.RabbitService;
import fr.rakambda.rsndiscord.spring.amqp.message.DeleteMessageDelayMessage;
import fr.rakambda.rsndiscord.spring.amqp.message.IDelayedMessage;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class DelayedMessageHandler{
	private static final Duration RETRY_DELAY = Duration.ofMinutes(15);
	
	private final JDA jda;
	private final RabbitService rabbitService;
	private final AmqpNameProvider amqpNameProvider;
	
	public DelayedMessageHandler(JDA jda, RabbitService rabbitService, AmqpNameProvider amqpNameProvider){
		this.jda = jda;
		this.rabbitService = rabbitService;
		this.amqpNameProvider = amqpNameProvider;
	}
	
	@RabbitListener(queues = "#{amqpNameProvider.getQueueDelayName()}")
	public void receive(@NonNull IDelayedMessage message, @Headers Map<String, Object> headers){
		CompletableFuture<?> future;
		if(message instanceof DeleteMessageDelayMessage deleteMessageDelayMessage){
			future = handleDeleteMessage(deleteMessageDelayMessage);
		}
		else{
			log.error("Received unknown delayed message of type {} : {}", message.getClass().getCanonicalName(), message);
			return;
		}
		
		future.exceptionally(e -> handleError(e, headers, message));
	}
	
	@Nullable
	private <T> T handleError(@NonNull Throwable throwable, @NonNull Map<String, Object> header, @NonNull IDelayedMessage message){
		log.warn("Failed to handle message", throwable);
		
		var retry = Optional.ofNullable(header.get(amqpNameProvider.getXRedeliverCountRemainingHeader()))
				.filter(Integer.class::isInstance)
				.map(Integer.class::cast)
				.orElse(0);
		if(retry <= 0){
			log.warn("Retry count for message is {}, not retrying", retry);
			return null;
		}
		
		rabbitService.scheduleMessage(RETRY_DELAY, retry - 1, message);
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
				.map(t -> JDAWrappers.delete(t).submit().thenApply(empty -> m))
				.orElseGet(() -> CompletableFuture.completedFuture(m));
	}
}
