package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

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
					Logger logger;
					if(target instanceof Guild g){
						logger = Log.getLogger(g);
					}
					else{
						logger = Log.getLogger();
					}
					
					logger.info("Deleted message {}", message);
				});
	}
}
