package fr.rakambda.rsndiscord.spring.interaction.button.impl;

import fr.rakambda.rsndiscord.spring.interaction.button.api.IExecutableButtonGuild;
import fr.rakambda.rsndiscord.spring.interaction.exception.InvalidChannelTypeException;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Component
public class TodoMessageKeepButtonHandler implements IExecutableButtonGuild{
	private static final String COMPONENT_ID = "todo-message-keep";
	
	@Override
	@NotNull
	public String getComponentId(){
		return COMPONENT_ID;
	}
	
	@NotNull
	@Override
	public CompletableFuture<?> executeGuild(@NotNull ButtonInteraction event, @NotNull Guild guild, @NotNull Member member) throws InvalidChannelTypeException{
		if(event.getChannelType() != ChannelType.GUILD_PUBLIC_THREAD){
			throw new InvalidChannelTypeException(event.getChannelType());
		}
		
		var message = event.getMessage();
		var threadChannel = event.getChannel().asThreadChannel();
		
		return event.deferEdit().submit()
				.thenCompose(empty -> JDAWrappers.delete(message).submit())
				.thenCompose(v -> JDAWrappers.editThread(threadChannel).setArchived(true).submit());
	}
	
	@NotNull
	public static Supplier<Button> builder(){
		return () -> Button.success(COMPONENT_ID, "Archive").withEmoji(Emoji.fromUnicode("U+1F4E6"));
	}
}
