package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class RemoveAllReactionWrapper{
	private final Object message;
	private final String reaction;
	private final RestAction<Void> action;
	
	public RemoveAllReactionWrapper(@NotNull MessageReaction reaction){
		message = reaction.getMessageId();
		this.reaction = reaction.getReactionEmote().toString();
		action = reaction.clearReactions();
	}
	
	public RemoveAllReactionWrapper(@NotNull Message message, @NotNull String emote){
		this.message = message;
		reaction = emote;
		action = message.clearReactions(emote);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> log.info("Removed all reactions {} from message {}", reaction, message));
	}
}
