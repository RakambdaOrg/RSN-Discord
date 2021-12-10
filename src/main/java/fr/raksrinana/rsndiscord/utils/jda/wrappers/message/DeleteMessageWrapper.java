package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class DeleteMessageWrapper{
	private final MessageChannel channel;
	private final String message;
	private final AuditableRestAction<Void> action;
	
	public DeleteMessageWrapper(@NotNull Message message){
		channel = message.getChannel();
		this.message = message.toString();
		action = message.delete();
	}
	
	public DeleteMessageWrapper(@NotNull MessageChannel channel, long messageId){
		this.channel = channel;
		message = Long.toString(messageId);
		action = channel.deleteMessageById(messageId);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> log.info("Deleted message {} in {}", message, channel));
	}
}
