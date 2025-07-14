package fr.rakambda.rsndiscord.spring.interaction.button.impl.weward;

import fr.rakambda.rsndiscord.spring.interaction.button.api.IExecutableButtonGuild;
import fr.rakambda.rsndiscord.spring.interaction.exception.InvalidChannelTypeException;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Component
public class WewardNoLongerNeededButtonHandler implements IExecutableButtonGuild{
	private static final String COMPONENT_ID = "weward-no-longer-needed";
	private static final MessageTopLevelComponent BUTTONS_ORIGINAL = ActionRow.of(
			WewardInterestButtonHandler.builder().get(),
			WewardDeleteButtonHandler.builder().get()
	);
	
	@Override
	@NotNull
	public String getComponentId(){
		return COMPONENT_ID;
	}
	
	@NotNull
	@Override
	public CompletableFuture<?> executeGuild(@NotNull ButtonInteraction event, @NotNull Guild guild, @NotNull Member member) throws InvalidChannelTypeException{
		var channel = event.getChannel();
		
		if(event.getChannelType() == ChannelType.GUILD_PUBLIC_THREAD){
			var threadChannel = channel.asThreadChannel();
			return event.deferReply(true).submit()
					.thenCompose(empty -> threadChannel.retrieveParentMessage().submit())
					.thenCompose(message -> JDAWrappers.editComponents(message, BUTTONS_ORIGINAL).submit())
					.thenCompose(empty -> JDAWrappers.delete(threadChannel).submit());
		}
		
		throw new InvalidChannelTypeException(event.getChannelType());
	}
	
	@NotNull
	public static Supplier<Button> builder(){
		return () -> Button.secondary(COMPONENT_ID, "No longer needed").withEmoji(Emoji.fromUnicode("U+1F6D1"));
	}
}
