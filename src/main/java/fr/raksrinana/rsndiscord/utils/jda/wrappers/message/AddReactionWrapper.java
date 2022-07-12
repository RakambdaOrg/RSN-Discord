package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class AddReactionWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final Message message;
	private final Emoji reaction;
	
	public AddReactionWrapper(@NotNull Message message, @NotNull Emoji reaction){
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
