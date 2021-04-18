package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import java.util.concurrent.CompletableFuture;

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
					Logger logger;
					if(target instanceof Guild g){
						logger = Log.getLogger(g);
					}
					else{
						logger = Log.getLogger();
					}
					
					logger.info("Unpinned message {}", message);
				});
	}
}
