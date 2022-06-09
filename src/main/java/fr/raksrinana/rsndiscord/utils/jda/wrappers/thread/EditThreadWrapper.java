package fr.raksrinana.rsndiscord.utils.jda.wrappers.thread;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.managers.channel.concrete.ThreadChannelManager;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class EditThreadWrapper extends ActionWrapper<Void, ThreadChannelManager>{
	private final ThreadChannel threadChannel;
	
	public EditThreadWrapper(@NotNull ThreadChannel threadChannel){
		super(threadChannel.getManager());
		this.threadChannel = threadChannel;
	}
	
	public EditThreadWrapper setAutoArchiveDuration(@NotNull ThreadChannel.AutoArchiveDuration duration){
		getAction().setAutoArchiveDuration(duration);
		return this;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Edited thread {}", threadChannel);
	}
	
	@NotNull
	public CompletableFuture<ThreadChannel> submitAndGet(){
		return submit().thenApply(empty -> threadChannel);
	}
}
