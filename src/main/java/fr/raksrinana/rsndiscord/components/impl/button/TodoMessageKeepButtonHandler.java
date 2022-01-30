package fr.raksrinana.rsndiscord.components.impl.button;

import fr.raksrinana.rsndiscord.components.ButtonHandler;
import fr.raksrinana.rsndiscord.components.ComponentResult;
import fr.raksrinana.rsndiscord.components.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.components.ComponentResult.HANDLED;

@Log4j2
@ButtonHandler
public class TodoMessageKeepButtonHandler extends SimpleButtonHandler{
	private static final String TODO_MESSAGE_ID = new TodoMessageDeleteButtonHandler().getId();
	
	public TodoMessageKeepButtonHandler(){
		super("todo-message-keep");
	}
	
	@NotNull
	@Override
	public CompletableFuture<ComponentResult> handleGuild(@NotNull ButtonInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		var message = event.getMessage();
		
		var buttons = message.getActionRows().stream()
				.findFirst().stream()
				.flatMap(a -> a.getComponents().stream())
				.map(c -> {
					if(c instanceof Button b){
						if(Objects.equals(b.getId(), TODO_MESSAGE_ID)){
							return b.asDisabled();
						}
						if(Objects.equals(b.getId(), getId())){
							return b.withLabel("Message archived").asDisabled();
						}
					}
					return c;
				})
				.toList();
		return JDAWrappers.editComponents(message, buttons).submit()
				.thenApply(empty -> HANDLED);
	}
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.success(getComponentId(), "Archive").withEmoji(Emoji.fromUnicode("U+1F4E6"));
	}
}
