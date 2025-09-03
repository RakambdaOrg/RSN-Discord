package fr.rakambda.rsndiscord.spring.interaction.button.impl.todo;

import fr.rakambda.rsndiscord.spring.interaction.button.api.IExecutableButtonGuild;
import fr.rakambda.rsndiscord.spring.interaction.exception.InvalidChannelTypeException;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Component
public class TodoMessageDeleteButtonHandler implements IExecutableButtonGuild{
	private static final String COMPONENT_ID = "todo-message-delete";
	
	@Override
	@NonNull
	public String getComponentId(){
		return COMPONENT_ID;
	}
	
	@NonNull
	@Override
	public CompletableFuture<?> executeGuild(@NonNull ButtonInteraction event, @NonNull Guild guild, @NonNull Member member) throws InvalidChannelTypeException{
		if(event.getChannelType() != ChannelType.GUILD_PUBLIC_THREAD){
			throw new InvalidChannelTypeException(event.getChannelType());
		}
		
		var deferred = event.deferEdit().submit();
		
		var channel = event.getChannel();
		var threadChannel = channel.asThreadChannel();
		if(threadChannel.getParentChannel().getType() == ChannelType.FORUM){
			return deferred.thenCompose(empty -> JDAWrappers.delete(threadChannel).submit());
		}
		
		return deferred.thenCompose(m -> handleThreadChannel(threadChannel));
	}
	
	@NonNull
	private CompletableFuture<Void> handleThreadChannel(@NonNull ThreadChannel threadChannel){
		return threadChannel.retrieveParentMessage().submit()
				.thenCompose(message -> JDAWrappers.delete(message).submit())
				.exceptionally(throwable -> {
					log.warn("Failed to delete thread parent message", throwable);
					return null;
				})
				.thenCompose(empty -> JDAWrappers.delete(threadChannel).submit());
	}
	
	@NonNull
	public static Supplier<Button> builder(){
		return () -> Button.danger(COMPONENT_ID, "Delete").withEmoji(Emoji.fromUnicode("U+1F5D1"));
	}
}
