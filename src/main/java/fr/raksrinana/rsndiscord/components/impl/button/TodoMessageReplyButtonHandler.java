package fr.raksrinana.rsndiscord.components.impl.button;

import fr.raksrinana.rsndiscord.components.ButtonHandler;
import fr.raksrinana.rsndiscord.components.ComponentResult;
import fr.raksrinana.rsndiscord.components.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.components.ComponentResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@Log4j2
@ButtonHandler
public class TodoMessageReplyButtonHandler extends SimpleButtonHandler{
	public TodoMessageReplyButtonHandler(){
		super("todo-message-reply");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ComponentResult> handle(@NotNull ButtonClickEvent event){
		var guild = event.getGuild();
		var user = event.getUser();
		var message = event.getMessage();
		
		return JDAWrappers.createTextChannel(guild, "reply-" + event.getMessageIdLong()).submit()
				.thenCompose(forwardChannel -> {
					Optional.ofNullable(message.getTextChannel().getParentCategory()).ifPresent(category -> JDAWrappers.edit(forwardChannel)
							.setParent(category)
							.sync(category)
							.submit());
					
					return JDAWrappers.message(forwardChannel, translate(guild, "reaction.original-from", message.getAuthor().getAsMention())).submit();
				})
				.thenCompose(sent -> JDAWrappers.message(sent.getTextChannel(), message)
						.clearActionRows()
						.submit())
				.thenCompose(sent -> JDAWrappers.message(sent.getTextChannel(), translate(guild, "reaction.react-archive", user.getAsMention()))
						.addActionRow(new ReplyChannelDeleteButtonHandler().asComponent())
						.submit())
				.thenCompose(sent -> JDAWrappers.delete(message).submit())
				.thenApply(e -> HANDLED);
	}
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.success(getComponentId(), "Reply").withEmoji(Emoji.fromUnicode("U+21A9"));
	}
}
