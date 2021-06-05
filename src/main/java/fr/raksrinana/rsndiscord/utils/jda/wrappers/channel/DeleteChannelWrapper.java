package fr.raksrinana.rsndiscord.utils.jda.wrappers.channel;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class DeleteChannelWrapper{
	private final Guild guild;
	private final TextChannel channel;
	private final AuditableRestAction<Void> action;
	
	public DeleteChannelWrapper(@NotNull Guild guild, @NotNull TextChannel channel){
		this.guild = guild;
		this.channel = channel;
		this.action = channel.delete();
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> log.info("Deleted channel {}", channel));
	}
}
