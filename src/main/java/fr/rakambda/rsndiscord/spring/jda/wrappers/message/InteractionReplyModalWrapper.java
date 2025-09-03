package fr.rakambda.rsndiscord.spring.jda.wrappers.message;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Slf4j
public class InteractionReplyModalWrapper extends ActionWrapper<Void, ModalCallbackAction>{
	private final ISnowflake target;
	
	public InteractionReplyModalWrapper(@Nullable ISnowflake target, @NonNull IModalCallback callback, @NonNull Modal modal){
		super(callback.replyModal(modal));
		this.target = target;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Replied with new modal to interaction in {}", target);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to reply modal in {}", target, throwable);
	}
}
