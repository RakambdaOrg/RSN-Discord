package fr.raksrinana.rsndiscord.interaction.component.button.impl;

import fr.raksrinana.rsndiscord.interaction.component.ComponentResult;
import fr.raksrinana.rsndiscord.interaction.component.button.api.ButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.utils.Utilities.MAIN_RAKSRINANA_ACCOUNT;

@Log4j2
@ButtonHandler
public class ExternalTodoCompletedButtonHandler extends SimpleButtonHandler{
	public ExternalTodoCompletedButtonHandler(){
		super("external-todo-completed");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ComponentResult> handleGuild(@NotNull ButtonInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		var user = event.getUser();
		var message = event.getMessage();
		
		return Optional.ofNullable(event.getJDA().getUserById(MAIN_RAKSRINANA_ACCOUNT))
				.map(User::openPrivateChannel)
				.map(RestAction::submit)
				.map(future -> future.thenCompose(privateChannel -> JDAWrappers.message(privateChannel, user.getAsMention() + " completed").submit()
						.thenCompose(m -> JDAWrappers.message(m.getPrivateChannel(), message).clearActionRows().submit())
						.thenCompose(m -> JDAWrappers.delete(message).submit())
						.thenApply(m -> ComponentResult.HANDLED)))
				.orElseGet(() -> CompletableFuture.completedFuture(ComponentResult.HANDLED));
	}
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.success(getComponentId(), "Complete").withEmoji(Emoji.fromUnicode("U+1F44C"));
	}
}
