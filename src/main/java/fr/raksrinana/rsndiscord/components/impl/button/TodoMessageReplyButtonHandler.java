package fr.raksrinana.rsndiscord.components.impl.button;

import fr.raksrinana.rsndiscord.components.ButtonHandler;
import fr.raksrinana.rsndiscord.components.ComponentResult;
import fr.raksrinana.rsndiscord.components.base.SimpleButtonHandler;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;
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
		var retryName = "reply-" + event.getMessageIdLong();
		
		return message.createThread(retryName).submit()
				.thenAccept(thread -> {
					thread.addThreadMember(user).submit();
					message.getMentionedMembers().forEach(u -> thread.addThreadMember(u).submit());
					thread.sendMessage(translate(guild, "reaction.react-archive", user.getAsMention())).submit();
					event.editButton(asComponent().asDisabled()).submit();
				})
				.thenApply(e -> HANDLED);
	}
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.success(getComponentId(), "Reply").withEmoji(Emoji.fromUnicode("U+21A9"));
	}
}
