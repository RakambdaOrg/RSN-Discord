package fr.raksrinana.rsndiscord.interaction.component.button.impl;

import fr.raksrinana.rsndiscord.interaction.component.ComponentResult;
import fr.raksrinana.rsndiscord.interaction.component.button.api.ButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
@ButtonHandler
public class TodoMessageKeepWithoutThreadButtonHandler extends SimpleButtonHandler{
	public TodoMessageKeepWithoutThreadButtonHandler(){
		super("todo-message-keep-no-thread");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ComponentResult> handleGuild(@NotNull ButtonInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		if(event.getChannelType() != ChannelType.GUILD_PUBLIC_THREAD){
			return CompletableFuture.completedFuture(ComponentResult.NOT_IMPLEMENTED);
		}
		
		var threadChannel = event.getChannel().asThreadChannel();
		var message = event.getMessage();
		
		if(threadChannel.getParentChannel().getType() == ChannelType.FORUM){
			return CompletableFuture.completedFuture(ComponentResult.NOT_IMPLEMENTED);
		}
		
		return JDAWrappers.delete(message).submit()
				.thenCompose(v -> JDAWrappers.delete(threadChannel).submit())
				.thenApply(empty -> ComponentResult.HANDLED);
	}
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.success(getComponentId(), "Archive (only message)").withEmoji(Emoji.fromUnicode("U+1F4E8"));
	}
}
