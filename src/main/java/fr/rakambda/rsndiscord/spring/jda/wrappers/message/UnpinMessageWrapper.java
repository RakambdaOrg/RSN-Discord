package fr.rakambda.rsndiscord.spring.jda.wrappers.message;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class UnpinMessageWrapper extends ActionWrapper<Void, RestAction<Void>>{
	private final Message message;
	
	public UnpinMessageWrapper(@NotNull Message message){
		super(message.unpin());
		this.message = message;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Unpinned message {}", message);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to unpin message {}", message, throwable);
	}
}
