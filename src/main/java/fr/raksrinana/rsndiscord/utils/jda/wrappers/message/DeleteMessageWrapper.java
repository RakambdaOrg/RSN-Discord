package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class DeleteMessageWrapper{
	private final ISnowflake target;
	private final String message;
	private final AuditableRestAction<Void> action;
	
	public DeleteMessageWrapper(@Nullable ISnowflake target, @NotNull Message message){
		this.target = target;
		this.message = message.toString();
		this.action = message.delete();
	}
	
	public DeleteMessageWrapper(@Nullable ISnowflake target, @NotNull TextChannel channel, long messageId){
		this.target = target;
		this.message = Long.toString(messageId);
		this.action = channel.deleteMessageById(messageId);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> {
					log.info("Deleted message {}", message);
				});
	}
}
