package fr.rakambda.rsndiscord.spring.event;

import fr.rakambda.rsndiscord.spring.amqp.RabbitService;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.log.LogContext;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static java.util.Objects.isNull;
import static net.dv8tion.jda.api.entities.MessageType.INLINE_REPLY;

@Component
@Slf4j
public class TimeReactionsReplyEventListener extends ListenerAdapter{
	private final RabbitService rabbitService;
	
	@Autowired
	public TimeReactionsReplyEventListener(RabbitService rabbitService){
		this.rabbitService = rabbitService;
	}
	
	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event){
		if(!event.isFromGuild()){
			return;
		}
		
		var guild = event.getGuild();
		var author = event.getAuthor();
		
		try(var ignored = LogContext.with(guild).with(author)){
			
			var message = event.getMessage();
			if(message.getType() != INLINE_REPLY || author.isBot()){
				return;
			}
			
			var messageReference = message.getMessageReference();
			if(isNull(messageReference)){
				return;
			}
			
			messageReference.resolve().submit()
					.thenApply(this::filterTimeReactionMessage)
					.thenApply(reference -> reply(reference, message));
		}
		catch(Exception e){
			log.error("Error handling message", e);
		}
	}
	
	@NotNull
	private Message filterTimeReactionMessage(@NotNull Message message){
		if(!message.getContentRaw().startsWith("§TR§")){
			throw new IllegalArgumentException("Message is not a time reaction");
		}
		return message;
	}
	
	@NotNull
	private CompletableFuture<?> reply(@NotNull Message reference, @NotNull Message message){
		log.info("Creating time-reaction reply for {}", message);
		
		var referenceTimes = Arrays.stream(reference.getContentRaw().split("\n"))
				.filter(line -> !line.isBlank())
				.filter(line -> Character.isDigit(line.charAt(0)) || line.startsWith("N/A"))
				.map(line -> line.split(" ", 2)[0])
				.toList();
		
		var receivedLines = Arrays.stream(message.getContentRaw().split("\n")).toList();
		
		if(referenceTimes.size() != receivedLines.size()){
			return JDAWrappers.message(message.getChannel(), String.format("Expected %d lines, got %d", referenceTimes.size(), receivedLines.size()))
					.replyTo(message)
					.submitAndDelete(2, rabbitService);
		}
		
		var response = IntStream.range(0, referenceTimes.size())
				.mapToObj(index -> referenceTimes.get(index) + " " + receivedLines.get(index))
				.collect(Collectors.joining("\n"));
		
		return JDAWrappers.reply(reference, "%s replied:\n\n%s".formatted(message.getAuthor().getAsMention(), response)).submit()
				.thenCompose(sent -> JDAWrappers.delete(message).submit());
	}
}
