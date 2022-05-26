package fr.raksrinana.rsndiscord.interaction.component.button.impl;

import fr.raksrinana.rsndiscord.interaction.component.ComponentResult;
import fr.raksrinana.rsndiscord.interaction.component.button.api.ButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Log4j2
@ButtonHandler
public class TodoMessageDeleteButtonHandler extends SimpleButtonHandler{
	public TodoMessageDeleteButtonHandler(){
		super("todo-message-delete");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ComponentResult> handleGuild(@NotNull ButtonInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		var channel = event.getChannel();
		
		if(channel instanceof ThreadChannel threadChannel){
			return handleThreadChannel(threadChannel);
		}
		else{
			return handleDefault(event.getMessage());
		}
	}
	
	@NotNull
	private CompletableFuture<ComponentResult> handleThreadChannel(@NotNull ThreadChannel threadChannel){
		return threadChannel.retrieveParentMessage().submit()
				.thenCompose(message -> JDAWrappers.delete(message).submit())
				.exceptionally(throwable -> {
					log.error("Failed to delete thread parrent message", throwable);
					return null;
				})
				.thenCompose(empty -> JDAWrappers.delete(threadChannel).submit())
				.thenApply(empty -> ComponentResult.HANDLED);
	}
	
	@NotNull
	private CompletableFuture<ComponentResult> handleDefault(@NotNull Message message){
		CompletableFuture<?> future = Optional.ofNullable(message.getMessageReference())
				.map(reference -> reference.resolve().submit().thenApply(m -> JDAWrappers.delete(m).submit()))
				.orElseGet(() -> CompletableFuture.completedFuture(null));
		
		var startedThread = message.getStartedThread();
		if(Objects.nonNull(startedThread)){
			future = future.thenCompose(empty -> JDAWrappers.delete(startedThread).submit())
					.exceptionally(throwable -> {
						log.error("Failed to delete thread {}", startedThread, throwable);
						return null;
					});
		}
		
		return future.thenCompose(empty -> JDAWrappers.delete(message).submit())
				.thenApply(empty -> ComponentResult.HANDLED);
	}
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.success(getComponentId(), "Delete").withEmoji(Emoji.fromUnicode("U+1F5D1"));
	}
}
