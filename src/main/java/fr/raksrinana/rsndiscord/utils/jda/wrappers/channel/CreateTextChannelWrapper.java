package fr.raksrinana.rsndiscord.utils.jda.wrappers.channel;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class CreateTextChannelWrapper{
	private final Guild guild;
	private final ChannelAction<TextChannel> action;
	
	public CreateTextChannelWrapper(@NotNull Guild guild, @NotNull String name){
		this.guild = guild;
		this.action = guild.createTextChannel(name);
	}
	
	public CompletableFuture<TextChannel> submit(){
		return action.submit().thenApply(channel -> {
			log.info("Created text channel {}", channel);
			return channel;
		});
	}
}
