package fr.rakambda.rsndiscord.spring.jda.wrappers.message;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.jspecify.annotations.NonNull;

@Slf4j
public class DeleteMessageWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final MessageChannel channel;
	private final String message;
	
	public DeleteMessageWrapper(@NonNull Message message){
		super(message.delete());
		channel = message.getChannel();
		this.message = "%s (author: %s)".formatted(message.getId(), message.getAuthor());
	}
	
	public DeleteMessageWrapper(@NonNull MessageChannel channel, long messageId){
		super(channel.deleteMessageById(messageId));
		this.channel = channel;
		message = Long.toString(messageId);
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Deleted message {} in {}", message, channel);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to delete message {} in {}", message, channel, throwable);
	}
}
