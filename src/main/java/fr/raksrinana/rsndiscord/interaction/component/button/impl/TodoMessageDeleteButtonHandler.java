package fr.raksrinana.rsndiscord.interaction.component.button.impl;

import fr.raksrinana.rsndiscord.interaction.component.ComponentResult;
import fr.raksrinana.rsndiscord.interaction.component.button.api.ButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
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
		
		if(channel.getType() == ChannelType.GUILD_PUBLIC_THREAD){
			var threadChannel = channel.asThreadChannel();
			if(threadChannel.getParentChannel().getType() == ChannelType.FORUM){
				return JDAWrappers.delete(threadChannel).submit()
						.thenApply(empty -> ComponentResult.HANDLED);
			}
			
			return handleThreadChannel(threadChannel);
		}
		
		return CompletableFuture.completedFuture(ComponentResult.NOT_IMPLEMENTED);
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
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.success(getComponentId(), "Archive").withEmoji(Emoji.fromUnicode("U+1F5D1"));
	}
}
