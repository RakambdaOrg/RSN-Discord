package fr.raksrinana.rsndiscord.utils.jda.wrappers;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.slf4j.Logger;
import java.util.concurrent.CompletableFuture;

public class DeleteMessageWrapper{
	private final ISnowflake target;
	private final Message message;
	private final AuditableRestAction<Void> action;
	
	public DeleteMessageWrapper(ISnowflake target, Message message){
		this.target = target;
		this.message = message;
		this.action = message.delete();
	}
	
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
