package fr.raksrinana.rsndiscord.utils.jda.wrappers.thread;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.managers.channel.concrete.ThreadChannelManager;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class EditThreadWrapper{
	private final ThreadChannel threadChannel;
	private final ThreadChannelManager threadChannelManager;
	
	public EditThreadWrapper(@NotNull ThreadChannel threadChannel){
		this.threadChannel = threadChannel;
		this.threadChannelManager = threadChannel.getManager();
	}
	
	public EditThreadWrapper setAutoArchiveDuration(@NotNull ThreadChannel.AutoArchiveDuration duration){
		threadChannelManager.setAutoArchiveDuration(duration);
		return this;
	}
	
	@NotNull
	public CompletableFuture<ThreadChannel> submit(){
		return threadChannelManager.submit()
				.thenApply(empty -> {
					log.info("Edited thread {}", threadChannel);
					
					return threadChannel;
				});
	}
}
