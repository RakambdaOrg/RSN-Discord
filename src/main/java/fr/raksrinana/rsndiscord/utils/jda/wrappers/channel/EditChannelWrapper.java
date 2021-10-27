package fr.raksrinana.rsndiscord.utils.jda.wrappers.channel;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.ChannelManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class EditChannelWrapper{
	private final TextChannel channel;
	private ChannelManager<TextChannel> action;
	
	public EditChannelWrapper(@NotNull TextChannel channel){
		this.channel = channel;
		action = channel.getManager();
	}
	@NotNull
	public EditChannelWrapper sync(@NotNull Category category){
		log.info("Will sync permissions of {} with {}", channel, category);
		action = action.sync(category);
		return this;
	}
	
	@NotNull
	public EditChannelWrapper setParent(@Nullable Category category){
		log.info("Will set {} in category {}", channel, category);
		action = action.setParent(category);
		return this;
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> log.info("Edited channel {}", channel));
	}
}
