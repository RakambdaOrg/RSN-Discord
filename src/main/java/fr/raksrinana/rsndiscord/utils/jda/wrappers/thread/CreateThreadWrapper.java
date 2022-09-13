package fr.raksrinana.rsndiscord.utils.jda.wrappers.thread;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class CreateThreadWrapper extends ActionWrapper<ThreadChannel, RestAction<ThreadChannel>>{
	private final Message message;
	
	public CreateThreadWrapper(@NotNull Message message, @NotNull String name){
		super(message.createThreadChannel(name));
		this.message = message;
	}
	
	@Override
	protected void logSuccess(ThreadChannel value){
		log.info("Created thread {} from {}", value, message);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to create thread from {}", message, throwable);
	}
}
