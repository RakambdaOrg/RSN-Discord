package fr.raksrinana.rsndiscord.utils.jda.wrappers.thread;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class DeleteThreadWrapper{
	private final ThreadChannel thread;
	private final AuditableRestAction<Void> action;
	
	public DeleteThreadWrapper(@NotNull ThreadChannel thread){
		this.thread = thread;
		action = thread.delete();
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> log.info("Deleted thread {}", thread));
	}
}
