package fr.raksrinana.rsndiscord.button.impl;

import fr.raksrinana.rsndiscord.button.ButtonHandler;
import fr.raksrinana.rsndiscord.button.ButtonResult;
import fr.raksrinana.rsndiscord.button.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.button.ButtonResult.HANDLED;

@Log4j2
@ButtonHandler
public class TodoMessageCompletedButtonHandler extends SimpleButtonHandler{
	public TodoMessageCompletedButtonHandler(){
		super("todo-message-completed");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ButtonResult> handle(@NotNull ButtonClickEvent event){
		var message = event.getMessage();
		
		return Optional.ofNullable(message.getReferencedMessage())
				.map(m -> JDAWrappers.delete(m).submit())
				.orElseGet(() -> CompletableFuture.completedFuture(null))
				.thenCompose(empty -> JDAWrappers.delete(message).submit())
				.thenApply(empty -> HANDLED);
	}
	
	@Override
	@NotNull
	public Button asButton(){
		return Button.success(getButtonId(), "Complete").withEmoji(Emoji.fromUnicode("U+1F44C"));
	}
}
