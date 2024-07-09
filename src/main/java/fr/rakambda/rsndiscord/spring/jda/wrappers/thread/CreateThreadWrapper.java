package fr.rakambda.rsndiscord.spring.jda.wrappers.thread;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class CreateThreadWrapper extends ActionWrapper<ThreadChannel, RestAction<ThreadChannel>>{
	private final Message message;
	
	public CreateThreadWrapper(@NotNull Message message, @NotNull String name){
		super(message.createThreadChannel(name));
		this.message = message;
	}
	
	@Override
	protected void logSuccess(ThreadChannel value){
		log.info("Created thread {} from {}", value, message.getId());
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to create thread from {}", message.getId(), throwable);
	}
}
