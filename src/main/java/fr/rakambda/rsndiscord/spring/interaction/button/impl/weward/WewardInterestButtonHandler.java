package fr.rakambda.rsndiscord.spring.interaction.button.impl.weward;

import fr.rakambda.rsndiscord.spring.interaction.button.api.IExecutableButtonGuild;
import fr.rakambda.rsndiscord.spring.interaction.exception.InvalidChannelTypeException;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Component
public class WewardInterestButtonHandler implements IExecutableButtonGuild{
	private static final String COMPONENT_ID = "weward-interest";
	private static final ItemComponent[] BUTTONS_NORMAL = {
			WewardTradedButtonHandler.builder().get(),
			WewardDeleteButtonHandler.builder().get()
	};
	
	private final LocalizationService localizationService;
	
	@Autowired
	public WewardInterestButtonHandler(LocalizationService localizationService){
		this.localizationService = localizationService;
	}
	
	@Override
	@NotNull
	public String getComponentId(){
		return COMPONENT_ID;
	}
	
	@NotNull
	@Override
	public CompletableFuture<?> executeGuild(@NotNull ButtonInteraction event, @NotNull Guild guild, @NotNull Member member) throws InvalidChannelTypeException{
		var deferred = event.deferEdit().submit();
		var locale = event.getUserLocale();
		
		var content = localizationService.translate(locale, "weward.card-interest",
				event.getMessage().getAuthor().getAsMention(),
				member.getAsMention()
		);
		return deferred.thenCompose(empty -> JDAWrappers
				.createThread(event.getMessage(), "trade-" + event.getMessageId()).submit()
				.thenCompose(thread -> JDAWrappers.message(thread, content)
						.setActionRows(ActionRow.of(BUTTONS_NORMAL))
						.submit()));
	}
	
	@NotNull
	public static Supplier<Button> builder(){
		return () -> Button.success(COMPONENT_ID, "Show interest").withEmoji(Emoji.fromUnicode("U+1F4E9"));
	}
}
