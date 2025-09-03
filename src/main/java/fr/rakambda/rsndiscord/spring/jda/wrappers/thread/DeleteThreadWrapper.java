package fr.rakambda.rsndiscord.spring.jda.wrappers.thread;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jspecify.annotations.NonNull;

@Slf4j
public class DeleteThreadWrapper extends ActionWrapper<Void, AuditableRestAction<Void>>{
	private final ThreadChannel thread;
	
	public DeleteThreadWrapper(@NonNull ThreadChannel thread){
		super(thread.delete());
		this.thread = thread;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Deleted thread {}", thread);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to delete thread {}", thread, throwable);
	}
}
