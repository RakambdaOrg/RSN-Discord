package fr.rakambda.rsndiscord.spring.interaction.button.impl.weward;

import fr.rakambda.rsndiscord.spring.interaction.button.api.IExecutableButtonGuild;
import fr.rakambda.rsndiscord.spring.interaction.exception.InvalidChannelTypeException;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Component
public class WewardInterestButtonHandler implements IExecutableButtonGuild{
	private static final String COMPONENT_ID = "weward-interest";
	private static final ActionRowChildComponent[] BUTTONS_NORMAL = {
			WewardTradedButtonHandler.builder().get(),
			WewardDeleteButtonHandler.builder().get(),
			WewardNoLongerNeededButtonHandler.builder().get()
	};
	private static final ActionRowChildComponent[] BUTTONS_ORIGINAL = {
			WewardInterestButtonHandler.builder().get().asDisabled(),
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
		var locale = event.getGuildLocale();
		
		if(Objects.nonNull(event.getMessage().getStartedThread())){
			var deferred = event.deferReply(true).submit();
			var content = localizationService.translate(locale, "weward.card-trade-already-started");
			return deferred.thenCompose(empty -> JDAWrappers.reply(event, content).submit());
		}
		
		var deferred = event.deferEdit().submit();
		var content = localizationService.translate(locale, "weward.card-interest",
				getOwner(event.getMessage()).map(UserSnowflake::getAsMention).orElse("Unknown"),
				member.getAsMention()
		);
		return deferred.thenCompose(hook -> JDAWrappers.editComponents(hook, ActionRow.of(BUTTONS_ORIGINAL)).submit())
				.thenCompose(hook -> JDAWrappers
						.createThread(event.getMessage(), "trade-" + event.getMessageId()).submit()
						.thenCompose(thread -> JDAWrappers.message(thread, content)
								.setActionRows(ActionRow.of(BUTTONS_NORMAL))
								.submit()));
	}
	
	@NotNull
	private Optional<UserSnowflake> getOwner(@NotNull Message message){
		return message.getEmbeds().stream()
				.filter(Objects::nonNull)
				.map(MessageEmbed::getFooter)
				.filter(Objects::nonNull)
				.map(MessageEmbed.Footer::getText)
				.filter(Objects::nonNull)
				.findFirst()
				.map(User::fromId);
	}
	
	@NotNull
	public static Supplier<Button> builder(){
		return () -> Button.success(COMPONENT_ID, "Show interest").withEmoji(Emoji.fromUnicode("U+1F4E9"));
	}
}
