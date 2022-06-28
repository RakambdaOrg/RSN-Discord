package fr.raksrinana.rsndiscord.interaction.component.button.impl;

import fr.raksrinana.rsndiscord.interaction.component.ComponentResult;
import fr.raksrinana.rsndiscord.interaction.component.button.api.ButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
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
					log.error("Failed to delete thread parent message", throwable);
					return null;
				})
				.thenCompose(empty -> JDAWrappers.delete(threadChannel).submit())
				.thenApply(empty -> ComponentResult.HANDLED);
	}
	
	@NotNull
	private CompletableFuture<ComponentResult> handleDefault(@NotNull Message message){
		var deleteReference = Optional.ofNullable(message.getMessageReference())
				.map(reference -> reference.resolve().submit().thenCompose(m -> JDAWrappers.delete(m).submit()))
				.orElseGet(() -> CompletableFuture.completedFuture(null));
		
		var deleteMessage = JDAWrappers.delete(message).submit();
		
		var deleteThread = Optional.ofNullable(message.getStartedThread())
				.map(thread -> JDAWrappers.delete(thread).submit()
						.exceptionally(throwable -> {
							log.error("Failed to delete thread {}", thread, throwable);
							return null;
						}))
				.orElse(CompletableFuture.completedFuture(null));
		
		return deleteReference
				.thenCompose(empty -> deleteMessage)
				.thenCompose(empty -> deleteThread)
				.thenApply(empty -> ComponentResult.HANDLED);
	}
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.success(getComponentId(), "Delete").withEmoji(Emoji.fromUnicode("U+1F5D1"));
	}
}
