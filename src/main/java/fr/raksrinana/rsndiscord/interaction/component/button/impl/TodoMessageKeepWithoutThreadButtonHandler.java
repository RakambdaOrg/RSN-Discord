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
import java.util.Objects;
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
		var message = event.getMessage();
		
		var buttons = message.getActionRows().stream()
				.findFirst().stream()
				.flatMap(a -> a.getComponents().stream())
				.map(c -> {
					if(c instanceof Button b){
						if(Objects.equals(b.getId(), getId())){
							return b.withLabel("Message kept").asDisabled();
						}
						else{
							return b.asDisabled();
						}
					}
					return c;
				})
				.toList();
		return JDAWrappers.editComponents(message, buttons).submit()
				.thenCompose(empty -> {
					if(message.getChannelType() == ChannelType.GUILD_PUBLIC_THREAD){
						return JDAWrappers.delete((ThreadChannel) message.getChannel()).submit();
					}
					return CompletableFuture.completedFuture(null);
				})
				.thenApply(empty -> ComponentResult.HANDLED);
	}
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.success(getComponentId(), "Keep (only message)").withEmoji(Emoji.fromUnicode("U+1F4E8"));
	}
}
