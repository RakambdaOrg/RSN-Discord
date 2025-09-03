package fr.rakambda.rsndiscord.spring.jda.wrappers.message;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import org.jspecify.annotations.NonNull;

@Slf4j
public class ClearReactionsWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final Message message;
	
	public ClearReactionsWrapper(@NonNull Message message){
		super(message.clearReactions());
		this.message = message;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Cleared reactions from message {}", message);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to clear all reactions from message {}", message, throwable);
	}
}
