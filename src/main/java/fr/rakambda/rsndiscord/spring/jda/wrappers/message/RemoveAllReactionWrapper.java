package fr.rakambda.rsndiscord.spring.jda.wrappers.message;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class RemoveAllReactionWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final Object message;
	private final Emoji reaction;
	
	public RemoveAllReactionWrapper(@NotNull MessageReaction reaction){
		super(reaction.clearReactions());
		message = reaction.getMessageId();
		this.reaction = reaction.getEmoji();
	}
	
	public RemoveAllReactionWrapper(@NotNull Message message, @NotNull Emoji emoji){
		super(message.clearReactions(emoji));
		this.message = message;
		reaction = emoji;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Removed all reactions {} from message {}", reaction, message);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to remove all reactions {} from message {}", reaction, message, throwable);
	}
}
