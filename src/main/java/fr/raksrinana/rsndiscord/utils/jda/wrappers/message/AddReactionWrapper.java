package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class AddReactionWrapper{
	private final Message message;
	private final String reaction;
	private final RestAction<Void> action;
	
	public AddReactionWrapper(@NotNull Message message, @NotNull String reaction){
		this.message = message;
		this.reaction = reaction;
		action = message.addReaction(reaction);
	}
	
	public AddReactionWrapper(@NotNull Message message, @NotNull Emote reaction){
		this.message = message;
		this.reaction = reaction.toString();
		action = message.addReaction(reaction);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> log.info("Added reaction {} to message {}", reaction, message));
	}
}
