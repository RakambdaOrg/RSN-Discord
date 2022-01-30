package fr.raksrinana.rsndiscord.interaction.component.button.impl;

import fr.raksrinana.rsndiscord.interaction.component.ComponentResult;
import fr.raksrinana.rsndiscord.interaction.component.button.api.ButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Log4j2
@ButtonHandler
@Deprecated
public class TodoMessageCompletedButtonHandler extends SimpleButtonHandler{
	public TodoMessageCompletedButtonHandler(){
		super("todo-message-completed");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ComponentResult> handleGuild(@NotNull ButtonInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		var message = event.getMessage();
		
		return Optional.ofNullable(message.getMessageReference())
				.map(r -> r.resolve().submit()
						.thenApply(m -> JDAWrappers.delete(m).submit()))
				.orElseGet(() -> CompletableFuture.completedFuture(null))
				.thenCompose(empty -> JDAWrappers.delete(message).submit())
				.thenApply(empty -> ComponentResult.HANDLED);
	}
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.success(getComponentId(), "Complete").withEmoji(Emoji.fromUnicode("U+1F44C"));
	}
}
