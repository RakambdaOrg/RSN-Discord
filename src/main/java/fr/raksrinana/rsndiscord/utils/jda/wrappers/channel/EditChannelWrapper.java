package fr.raksrinana.rsndiscord.utils.jda.wrappers.channel;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.GuildMessageChannel;
import net.dv8tion.jda.api.managers.channel.ChannelManager;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class EditChannelWrapper{
	private final GuildMessageChannel channel;
	private ChannelManager<?, ?> action;
	
	public EditChannelWrapper(@NotNull GuildMessageChannel channel){
		this.channel = channel;
		action = channel.getManager();
	}
	
	@NotNull
	public EditChannelWrapper setName(@NotNull String name){
		log.info("Will set {} with name {}", channel, name);
		action = action.setName(name);
		return this;
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> log.info("Edited channel {}", channel));
	}
}
