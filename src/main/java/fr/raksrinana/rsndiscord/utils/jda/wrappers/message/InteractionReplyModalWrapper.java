package fr.raksrinana.rsndiscord.utils.jda.wrappers.message;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class InteractionReplyModalWrapper{
	private final ISnowflake target;
	private ModalCallbackAction action;
	
	public InteractionReplyModalWrapper(@Nullable ISnowflake target, @NotNull IModalCallback callback, @NotNull Modal modal){
		this.target = target;
		action = callback.replyModal(modal);
	}
	
	@NotNull
	public CompletableFuture<Void> submit(){
		return action.submit()
				.thenApply(empty -> {
					log.info("Replied with new modal to slash command in {}", target);
					return empty;
				});
	}
}
