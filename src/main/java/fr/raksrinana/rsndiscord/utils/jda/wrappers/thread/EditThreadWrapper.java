package fr.raksrinana.rsndiscord.utils.jda.wrappers.thread;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.managers.channel.concrete.ThreadChannelManager;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class EditThreadWrapper extends ActionWrapper<Void, ThreadChannelManager>{
	private final ThreadChannel thread;
	
	public EditThreadWrapper(@NotNull ThreadChannel thread){
		super(thread.getManager());
		this.thread = thread;
	}
	
	public EditThreadWrapper setAutoArchiveDuration(@NotNull ThreadChannel.AutoArchiveDuration duration){
		getAction().setAutoArchiveDuration(duration);
		return this;
	}
	
	public EditThreadWrapper setArchived(boolean archived){
		getAction().setArchived(archived);
		return this;
	}
	
	public EditThreadWrapper setLocked(boolean locked){
		getAction().setLocked(locked);
		return this;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Edited thread {}", thread);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to edit thread {}", thread, throwable);
	}
	
	@NotNull
	public CompletableFuture<ThreadChannel> submitAndGet(){
		return submit().thenApply(empty -> thread);
	}
}
