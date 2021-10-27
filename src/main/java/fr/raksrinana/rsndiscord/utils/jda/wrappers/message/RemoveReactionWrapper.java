package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class RemoveReactionWrapper{
	private final String message;
	private final String reaction;
	private final RestAction<Void> action;
	
	public RemoveReactionWrapper(@NotNull MessageReaction reaction){
		message = reaction.getMessageId();
		this.reaction = reaction.getReactionEmote().toString();
		action = reaction.removeReaction();
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> log.info("Removed reaction {} from message {}", reaction, message));
	}
}
