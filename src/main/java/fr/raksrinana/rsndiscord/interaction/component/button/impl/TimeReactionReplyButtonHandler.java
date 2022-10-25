package fr.raksrinana.rsndiscord.interaction.component.button.impl;

import fr.raksrinana.rsndiscord.interaction.component.ComponentResult;
import fr.raksrinana.rsndiscord.interaction.component.button.api.ButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.interaction.modal.impl.TimeReactionReplyModal;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@Log4j2
@ButtonHandler
public class TimeReactionReplyButtonHandler extends SimpleButtonHandler{
	public TimeReactionReplyButtonHandler(){
		super("time-reaction-reply");
	}
	
	@Override
	public boolean deferReply(){
		return false;
	}
	
	@NotNull
	@Override
	public CompletableFuture<ComponentResult> handleGuild(@NotNull ButtonInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		return JDAWrappers.reply(event, new TimeReactionReplyModal(event.getMessage().getContentRaw()).asComponent()).submit()
				.thenApply(empty -> ComponentResult.HANDLED);
	}
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.primary(getComponentId(), "Reply").withEmoji(Emoji.fromUnicode("U+21A9"));
	}
}
