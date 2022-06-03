package fr.raksrinana.rsndiscord.interaction.component.button.impl;

import fr.raksrinana.rsndiscord.interaction.component.ComponentResult;
import fr.raksrinana.rsndiscord.interaction.component.button.api.ButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.base.SimpleButtonHandler;
import fr.raksrinana.rsndiscord.interaction.modal.impl.TodoMessageRenameModal;
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

@Log4j2
@ButtonHandler
public class TodoMessageRenameButtonHandler extends SimpleButtonHandler{
	public TodoMessageRenameButtonHandler(){
		super("todo-message-rename");
	}
	
	@Override
	public boolean deferReply(){
		return false;
	}
	
	@NotNull
	@Override
	public CompletableFuture<ComponentResult> handleGuild(@NotNull ButtonInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		var message = event.getMessage();
		
		var newButtons = message.getActionRows().stream()
				.findFirst().stream()
				.flatMap(a -> a.getComponents().stream())
				.map(c -> {
					if(c instanceof Button b){
						if(Objects.equals(b.getId(), getId())){
							return b.withLabel("Renamed").asDisabled();
						}
					}
					return c;
				})
				.toList();
		
		return JDAWrappers.editComponents(message, newButtons).submit()
				.thenCompose(empty -> JDAWrappers.reply(event, new TodoMessageRenameModal().asComponent()).submit())
				.thenApply(empty -> ComponentResult.HANDLED);
	}
	
	@Override
	@NotNull
	public Button asComponent(){
		return Button.primary(getComponentId(), "Rename").withEmoji(Emoji.fromUnicode("U+270D"));
	}
}
