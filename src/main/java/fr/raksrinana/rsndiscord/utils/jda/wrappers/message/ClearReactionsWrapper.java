package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class ClearReactionsWrapper{
	private final Message message;
	private final RestAction<Void> action;
	
	public ClearReactionsWrapper(@NotNull Message message){
		this.message = message;
		action = message.clearReactions();
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> log.info("Cleared reactions from message {}", message));
	}
}
