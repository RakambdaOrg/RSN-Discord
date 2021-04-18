package fr.raksrinana.rsndiscord.utils.jda.wrappers.channel;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public class CreateTextChannelWrapper{
	private final Guild guild;
	private final ChannelAction<TextChannel> action;
	
	public CreateTextChannelWrapper(@NotNull Guild guild, @NotNull String name){
		this.guild = guild;
		this.action = guild.createTextChannel(name);
	}
	
	public CompletableFuture<TextChannel> submit(){
		return action.submit()
				.thenApply(channel -> {
					Log.getLogger(guild).info("Created text channel {}", channel);
					return channel;
				});
	}
}
