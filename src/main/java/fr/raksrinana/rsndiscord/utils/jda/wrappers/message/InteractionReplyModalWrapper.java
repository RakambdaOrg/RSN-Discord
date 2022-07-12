package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Log4j2
public class InteractionReplyModalWrapper extends ActionWrapper<Void, ModalCallbackAction>{
	private final ISnowflake target;
	
	public InteractionReplyModalWrapper(@Nullable ISnowflake target, @NotNull IModalCallback callback, @NotNull Modal modal){
		super(callback.replyModal(modal));
		this.target = target;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Replied with new modal to slash command in {}", target);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to reply modal in {}", target, throwable);
	}
}
