package fr.raksrinana.rsndiscord.utils.jda.wrappers.thread;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class DeleteThreadWrapper extends ActionWrapper<Void, AuditableRestAction<Void>>{
	private final ThreadChannel thread;
	
	public DeleteThreadWrapper(@NotNull ThreadChannel thread){
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
