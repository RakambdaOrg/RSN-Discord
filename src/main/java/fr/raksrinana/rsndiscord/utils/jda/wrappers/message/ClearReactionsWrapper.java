package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class ClearReactionsWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final Message message;
	
	public ClearReactionsWrapper(@NotNull Message message){
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
