package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class UnpinMessageWrapper{
	private final ISnowflake target;
	private final Message message;
	private final RestAction<Void> action;
	
	public UnpinMessageWrapper(@Nullable ISnowflake target, @NotNull Message message){
		this.target = target;
		this.message = message;
		this.action = message.unpin();
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenAccept(empty -> {
					log.info("Unpinned message {}", message);
				});
	}
}
