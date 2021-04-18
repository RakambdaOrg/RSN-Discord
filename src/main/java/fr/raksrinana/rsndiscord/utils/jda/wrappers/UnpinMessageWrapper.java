package fr.raksrinana.rsndiscord.utils.jda.wrappers;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

public class UnpinMessageWrapper{
	private final Guild guild;
	private final Message message;
	private final RestAction<Void> action;
	
	public UnpinMessageWrapper(@NotNull Guild guild, @NotNull Message message){
		this.guild = guild;
		this.message = message;
		this.action = message.unpin();
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> Log.getLogger(guild).info("Unpinned message {}", message));
	}
}
