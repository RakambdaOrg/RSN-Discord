package fr.rakambda.rsndiscord.spring.jda.wrappers.message;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.requests.RestAction;
import org.jspecify.annotations.NonNull;

@Slf4j
public class AddReactionWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final Message message;
	private final Emoji reaction;
	
	public AddReactionWrapper(@NonNull Message message, @NonNull Emoji reaction){
		super(message.addReaction(reaction));
		this.message = message;
		this.reaction = reaction;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Added reaction {} to message {}", reaction, message);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to add reaction {} to message {}", reaction, message, throwable);
	}
}
