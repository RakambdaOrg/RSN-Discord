package fr.raksrinana.rsndiscord.utils.jda.wrappers.thread;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.GuildThread;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class CreateThreadWrapper{
	private final Message message;
	private final RestAction<GuildThread> action;
	
	public CreateThreadWrapper(@NotNull Message message, @NotNull String name){
		this.message = message;
		action = message.createThread(name);
	}
	
	@NotNull
	public CompletableFuture<GuildThread> submit(){
		return action.submit()
				.thenApply(thread -> {
					log.info("Created thread {} from {}", thread, message);
					
					return thread;
				});
	}
}
