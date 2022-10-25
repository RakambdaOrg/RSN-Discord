package fr.raksrinana.rsndiscord.interaction.modal.impl;

import fr.raksrinana.rsndiscord.interaction.component.button.impl.TimeReactionReplyButtonHandler;
import fr.raksrinana.rsndiscord.interaction.modal.ModalResult;
import fr.raksrinana.rsndiscord.interaction.modal.api.ModalHandler;
import fr.raksrinana.rsndiscord.interaction.modal.base.SimpleModalHandler;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.lang.Character.isDigit;

@Log4j2
@ModalHandler
public class TimeReactionCreateModal extends SimpleModalHandler{
	private static final String BODY_ID = "body";
	private static final String EPISODE_ID = "episode";
	private static final String LINK_ID = "link";
	
	private static final String NO_SPOILER_STR = "--";
	
	public TimeReactionCreateModal(){
		super("time-reaction-create");
	}
	
	@Override
	@NotNull
	public CompletableFuture<ModalResult> handleGuild(@NotNull ModalInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		var content = event.getValue(BODY_ID).getAsString();
		
		try{
			var reactions = Arrays.stream(content.split("\n")).collect(Collectors.toCollection(LinkedList::new));
			var messageContent = new StringBuilder();
			
			Optional.ofNullable(event.getValue(EPISODE_ID))
					.map(ModalMapping::getAsString)
					.filter(s -> !s.isBlank())
					.ifPresent(episode -> messageContent.append("§__**EP ").append(episode).append("**__\n"));
			
			Optional.ofNullable(event.getValue(LINK_ID))
					.map(ModalMapping::getAsString)
					.filter(s -> !s.isBlank())
					.ifPresent(link -> messageContent.append("§ ").append(link).append("\n"));
			
			reactions.stream()
					.filter(reaction -> !reaction.isBlank())
					.forEach(reaction -> {
						var decorator = "||";
						
						if(reaction.startsWith(NO_SPOILER_STR)){
							reaction = reaction.substring(NO_SPOILER_STR.length());
							decorator = "";
						}
						
						messageContent.append("\n");
						
						var parts = reaction.trim().split(" ", 2);
						if(parts.length < 2){
							throw new IllegalArgumentException("At least 2 parts are required on a line");
						}
						else{
							var time = convertTime(parts[0]);
							messageContent.append(time).append(decorator).append(parts[1]).append(decorator);
						}
					});
			
			return JDAWrappers.reply(event, messageContent.toString())
					.addActionRow(List.of(new TimeReactionReplyButtonHandler().asComponent()))
					.submit()
					.thenAccept(message -> Settings.get(guild).getMediaReactionMessages().add(new MessageConfiguration(message)))
					.thenApply(empty -> ModalResult.HANDLED)
					.exceptionally(error -> {
						JDAWrappers.reply(event, "Failed to send message: " + error.getMessage()).submit();
						return ModalResult.FAILED;
					});
		}
		catch(Exception e){
			log.error("Failed to parse anime reaction", e);
			return JDAWrappers.reply(event, translate(guild, "media-reaction.parse-error")).submit()
					.thenApply(message -> ModalResult.FAILED);
		}
	}
	
	@NotNull
	private static String convertTime(@NotNull String string) throws IllegalArgumentException{
		if(string.isBlank() || string.chars().anyMatch(c -> !isDigit(c))){
			throw new IllegalArgumentException("Failed to parse '" + string + "', not a proper time");
		}
		
		try{
			if(string.length() <= 2){
				return String.format("00:%02d ", Integer.parseInt(string));
			}
			if(string.length() <= 4){
				var cut = string.length() - 2;
				return String.format("%02d:%02d ", Integer.parseInt(string.substring(0, cut)), Integer.parseInt(string.substring(cut)));
			}
			var cut = string.length() - 4;
			return String.format("%02d:%02d:%02d ",
					Integer.parseInt(string.substring(0, cut)),
					Integer.parseInt(string.substring(cut, string.length() - 2)),
					Integer.parseInt(string.substring(string.length() - 2)));
		}
		catch(Exception e){
			throw new IllegalArgumentException("Failed to parse " + string, e);
		}
	}
	
	@NotNull
	public Modal asComponent(){
		var episode = TextInput.create(EPISODE_ID, "Episode", TextInputStyle.SHORT)
				.setRequired(false)
				.build();
		
		var link = TextInput.create(LINK_ID, "Link", TextInputStyle.SHORT)
				.setRequired(false)
				.build();
		
		var body = TextInput.create(BODY_ID, "Comments", TextInputStyle.PARAGRAPH)
				.setRequired(true)
				.setPlaceholder("Start a line with " + NO_SPOILER_STR + " to not mark as spoiler")
				.build();
		
		return Modal.create(getComponentId(), "Time reaction create")
				.addActionRows(
						ActionRow.of(episode),
						ActionRow.of(link),
						ActionRow.of(body)
				)
				.build();
	}
}
