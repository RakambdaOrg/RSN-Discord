package fr.rakambda.rsndiscord.spring.jda.wrappers.thread;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel.AutoArchiveDuration;
import net.dv8tion.jda.api.entities.channel.forums.ForumTagSnowflake;
import net.dv8tion.jda.api.managers.channel.concrete.ThreadChannelManager;
import org.jspecify.annotations.NonNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class EditThreadWrapper extends ActionWrapper<Void, ThreadChannelManager>{
	private final ThreadChannel thread;
	private final Collection<ForumTagSnowflake> currentTags;
	
	public EditThreadWrapper(@NonNull ThreadChannel thread){
		super(thread.getManager());
		this.thread = thread;
		
		currentTags = new ArrayList<>(thread.getAppliedTags());
	}
	
	@NonNull
	public EditThreadWrapper setAutoArchiveDuration(@NonNull AutoArchiveDuration duration){
		getAction().setAutoArchiveDuration(duration);
		return this;
	}
	
	@NonNull
	public EditThreadWrapper setArchived(boolean archived){
		getAction().setArchived(archived);
		return this;
	}
	
	@NonNull
	public EditThreadWrapper setLocked(boolean locked){
		getAction().setLocked(locked);
		return this;
	}
	
	@NonNull
	public EditThreadWrapper addTag(@NonNull ForumTagSnowflake tag){
		currentTags.add(tag);
		getAction().setAppliedTags(currentTags);
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
	
	@NonNull
	public CompletableFuture<ThreadChannel> submitAndGet(){
		return submit().thenApply(empty -> thread);
	}
}
