package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

@Log4j2
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
}
