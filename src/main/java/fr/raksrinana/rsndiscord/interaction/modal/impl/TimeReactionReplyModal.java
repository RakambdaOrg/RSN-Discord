package fr.raksrinana.rsndiscord.interaction.modal.impl;

import fr.raksrinana.rsndiscord.interaction.modal.ModalResult;
import fr.raksrinana.rsndiscord.interaction.modal.api.ModalHandler;
import fr.raksrinana.rsndiscord.interaction.modal.base.SimpleModalHandler;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.interaction.modal.ModalResult.HANDLED;

@Log4j2
@ModalHandler
public class TimeReactionReplyModal extends SimpleModalHandler{
	private static final String CONTENT_ID = "content_id";
	
	private final String body;
	
	public TimeReactionReplyModal(){
		this(null);
	}
	
	public TimeReactionReplyModal(@Nullable String body){
		super("time-reaction-reply");
		this.body = Objects.isNull(body)
				? null
				: body.lines()
				.filter(line -> !line.isBlank())
				.filter(line -> !line.startsWith("§"))
				.map(line -> line.replace("||", ""))
				.collect(Collectors.joining("\n"));
	}
	
	@Override
	@NotNull
	public CompletableFuture<ModalResult> handleGuild(@NotNull ModalInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		return JDAWrappers.reply(event, "Reply from %s:\n\n%s".formatted(
						member.getAsMention(),
						event.getValue(CONTENT_ID).getAsString()))
				.ephemeral(false)
				.submit()
				.thenApply(empty -> HANDLED);
	}
	
	@Override
	@NotNull
	public Modal asComponent(){
		var content = TextInput.create(CONTENT_ID, "Content", TextInputStyle.PARAGRAPH)
				.setValue(body)
				.setRequired(true)
				.build();
		
		return Modal.create(getComponentId(), "Time reaction reply")
				.addActionRows(ActionRow.of(content))
				.build();
	}
}
