package fr.rakambda.rsndiscord.spring.interaction.button.impl.todo;

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
public class TodoMessageKeepWithoutThreadButtonHandler implements IExecutableButtonGuild{
	private static final String COMPONENT_ID = "todo-message-keep-no-thread";
	
	@Override
	@NotNull
	public String getComponentId(){
		return COMPONENT_ID;
	}
	
	@NotNull
	@Override
	public CompletableFuture<?> executeGuild(@NotNull ButtonInteraction event, @NotNull Guild guild, @NotNull Member member) throws InvalidChannelTypeException{
		var channelType = event.getChannelType();
		if(channelType != ChannelType.GUILD_PUBLIC_THREAD){
			throw new InvalidChannelTypeException(channelType);
		}
		
		var threadChannel = event.getChannel().asThreadChannel();
		
		var parentChannelType = threadChannel.getParentChannel().getType();
		if(parentChannelType == ChannelType.FORUM){
			throw new InvalidChannelTypeException(parentChannelType);
		}
		
		var message = event.getMessage();
		return event.deferEdit().submit()
				.thenCompose(empty -> JDAWrappers.delete(message).submit())
				.thenCompose(v -> JDAWrappers.delete(threadChannel).submit());
	}
	
	@NotNull
	public static Supplier<Button> builder(){
		return () -> Button.primary(COMPONENT_ID, "Archive (only message)").withEmoji(Emoji.fromUnicode("U+1F4E8"));
	}
}
