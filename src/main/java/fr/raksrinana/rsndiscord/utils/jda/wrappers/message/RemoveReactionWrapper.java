package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import java.util.concurrent.CompletableFuture;

public class RemoveReactionWrapper{
	private final ISnowflake target;
	private final String message;
	private final String reaction;
	private final RestAction<Void> action;
	
	public RemoveReactionWrapper(@Nullable ISnowflake target, @NotNull Message message, @NotNull String reaction){
		this.target = target;
		this.message = message.toString();
		this.reaction = reaction;
		this.action = message.removeReaction(reaction);
	}
	
	public RemoveReactionWrapper(ISnowflake target, MessageReaction reaction){
		this.target = target;
		this.message = reaction.getMessageId();
		this.reaction = reaction.getReactionEmote().toString();
		this.action = reaction.removeReaction();
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
					
					logger.info("Removed reaction {} from message {}", reaction, message);
				});
	}
}
