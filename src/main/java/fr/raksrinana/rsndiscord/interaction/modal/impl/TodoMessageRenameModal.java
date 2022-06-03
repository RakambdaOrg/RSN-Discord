package fr.raksrinana.rsndiscord.interaction.modal.impl;

import fr.raksrinana.rsndiscord.interaction.modal.ModalResult;
import fr.raksrinana.rsndiscord.interaction.modal.api.ModalHandler;
import fr.raksrinana.rsndiscord.interaction.modal.base.SimpleModalHandler;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.interaction.modal.ModalResult.HANDLED;

@Log4j2
@ModalHandler
public class TodoMessageRenameModal extends SimpleModalHandler{
	private static final String NAME_ID = "name_id";
	
	public TodoMessageRenameModal(){
		super("todo-message-rename");
	}
	
	@Override
	@NotNull
	public CompletableFuture<ModalResult> handleGuild(@NotNull ModalInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		if(event.getChannelType() != ChannelType.GUILD_PUBLIC_THREAD){
			return CompletableFuture.completedFuture(HANDLED);
		}
		
		var thread = event.getThreadChannel();
		
		return JDAWrappers.edit(thread)
				.setName(event.getValue(NAME_ID).getAsString())
				.submit()
				.thenApply(empty -> HANDLED);
	}
	
	@Override
	@NotNull
	public Modal asComponent(){
		var content = TextInput.create(NAME_ID, "Content", TextInputStyle.SHORT)
				.setMaxLength(100)
				.setRequired(true)
				.build();
		
		return Modal.create(getComponentId(), "Rename kept thread")
				.addActionRows(ActionRow.of(content))
				.build();
	}
}
