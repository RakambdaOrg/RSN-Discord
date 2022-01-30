package fr.raksrinana.rsndiscord.interaction.component.button.impl;

import fr.raksrinana.rsndiscord.interaction.component.ComponentResult;
import fr.raksrinana.rsndiscord.interaction.component.button.api.ButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@Log4j2
@ButtonHandler
public class TodoMessageReplyButtonHandler extends SimpleButtonHandler{
	public TodoMessageReplyButtonHandler(){
		super("todo-message-reply");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ComponentResult> handleGuild(@NotNull ButtonInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		var user = event.getUser();
		var message = event.getMessage();
		var replyName = "reply-" + event.getMessageIdLong();
		
		return JDAWrappers.createThread(message, replyName).submit()
				.thenCompose(thread -> CompletableFuture.allOf(
						addMembersToThread(event, thread),
						JDAWrappers.message(thread, translate(guild, "reaction.react-archive", user.getAsMention())).submit(),
						event.editButton(asComponent().withLabel("Already replied").asDisabled()).submit()
				))
				.thenApply(e -> ComponentResult.HANDLED);
	}
	
	@NotNull
	private CompletableFuture<Void> addMembersToThread(@NotNull ButtonInteractionEvent event, @NotNull ThreadChannel thread){
		var authorFuture = Stream.of(JDAWrappers.addThreadMember(thread, event.getUser()).submit());
		var mentionedFutures = event.getMessage().getMentionedMembers().stream()
				.map(u -> JDAWrappers.addThreadMember(thread, u).submit());
		
		var futures = Stream.concat(authorFuture, mentionedFutures).toArray(CompletableFuture[]::new);
		return CompletableFuture.allOf(futures);
	}
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.success(getComponentId(), "Reply").withEmoji(Emoji.fromUnicode("U+21A9"));
	}
}
