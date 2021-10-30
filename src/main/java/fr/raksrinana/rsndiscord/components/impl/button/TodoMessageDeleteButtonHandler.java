package fr.raksrinana.rsndiscord.components.impl.button;

import fr.raksrinana.rsndiscord.components.ButtonHandler;
import fr.raksrinana.rsndiscord.components.ComponentResult;
import fr.raksrinana.rsndiscord.components.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.components.ComponentResult.HANDLED;

@Log4j2
@ButtonHandler
public class TodoMessageDeleteButtonHandler extends SimpleButtonHandler{
	public TodoMessageDeleteButtonHandler(){
		super("todo-message-delete");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ComponentResult> handle(@NotNull ButtonClickEvent event){
		var message = event.getMessage();
		var threadName = "reply-" + message.getIdLong();
		
		return Optional.ofNullable(message.getMessageReference())
				.map(reference -> reference.resolve().submit().thenApply(m -> JDAWrappers.delete(m).submit()))
				.orElseGet(() -> CompletableFuture.completedFuture(null))
				.thenCompose(empty -> Utilities.getThreadByName(event.getGuild(), threadName))
				.thenCompose(thread -> JDAWrappers.delete(thread).submit())
				.exceptionally(throwable -> {
					log.error("Failed to deleted thread {}", threadName, throwable);
					return null;
				})
				.thenCompose(empty -> JDAWrappers.delete(message).submit())
				.thenApply(empty -> HANDLED);
	}
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.success(getComponentId(), "Delete").withEmoji(Emoji.fromUnicode("U+1F5D1"));
	}
}
