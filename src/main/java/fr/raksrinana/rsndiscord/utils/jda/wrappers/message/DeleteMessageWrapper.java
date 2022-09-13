package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class DeleteMessageWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final MessageChannel channel;
	private final String message;
	
	public DeleteMessageWrapper(@NotNull Message message){
		super(message.delete());
		channel = message.getChannel();
		this.message = message.toString();
	}
	
	public DeleteMessageWrapper(@NotNull MessageChannel channel, long messageId){
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
