package fr.rakambda.rsndiscord.spring.interaction.modal.impl;

import fr.rakambda.rsndiscord.spring.interaction.exception.InvalidFormatException;
import fr.rakambda.rsndiscord.spring.interaction.exception.InvalidTimeFormatException;
import fr.rakambda.rsndiscord.spring.interaction.modal.api.IExecutableModalGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import static java.lang.Character.isDigit;

@Slf4j
@Component
public class TimeReactionCreateModal implements IExecutableModalGuild{
	private static final String COMPONENT_ID = "time-reaction-create";
	
	private static final String BODY_OPTION = "body";
	private static final String EPISODE_OPTION = "episode";
	private static final String LINK_OPTION = "link";
	
	private static final String NO_SPOILER_STR = "--";
	
	@Override
	@NotNull
	public String getComponentId(){
		return COMPONENT_ID;
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull ModalInteraction event, @NotNull Guild guild, @NotNull Member member) throws InvalidTimeFormatException, InvalidFormatException{
		var deferred = event.deferReply(false).submit();
		var content = event.getValue(BODY_OPTION).getAsString();
		
		var reactions = Arrays.stream(content.split("\n")).collect(Collectors.toCollection(LinkedList::new));
		var messageContent = new StringBuilder("§TR§\n");
		
		Optional.ofNullable(event.getValue(EPISODE_OPTION))
				.map(ModalMapping::getAsString)
				.filter(s -> !s.isBlank())
				.ifPresent(episode -> messageContent.append("§__**EP ").append(episode).append("**__\n"));
		
		Optional.ofNullable(event.getValue(LINK_OPTION))
				.map(ModalMapping::getAsString)
				.filter(s -> !s.isBlank())
				.ifPresent(link -> messageContent.append("§ ").append(link).append("\n"));
		
		for(var reaction : reactions){
			if(reaction.isBlank()){
				continue;
			}
			
			var decorator = "||";
			
			if(reaction.startsWith(NO_SPOILER_STR)){
				reaction = reaction.substring(NO_SPOILER_STR.length());
				decorator = "";
			}
			
			messageContent.append("\n");
			
			var parts = reaction.trim().split(" ", 2);
			if(parts.length < 2){
				throw new InvalidFormatException("At least 2 parts are required on a line");
			}
			else{
				var time = convertTime(parts[0]);
				messageContent.append(time).append(decorator).append(parts[1]).append(decorator);
			}
		}
		
		return deferred.thenCompose(empty -> JDAWrappers.reply(event, messageContent.toString()).submit());
	}
	
	@NotNull
	private static String convertTime(@NotNull String string) throws InvalidTimeFormatException{
		if(string.isBlank() || string.chars().anyMatch(c -> !isDigit(c))){
			throw new InvalidTimeFormatException(string);
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
	public static Supplier<Modal> builder(){
		return () -> {
			var episode = TextInput.create(EPISODE_OPTION, "Episode", TextInputStyle.SHORT)
					.setRequired(false)
					.build();
			
			var link = TextInput.create(LINK_OPTION, "Link", TextInputStyle.SHORT)
					.setRequired(false)
					.build();
			
			var body = TextInput.create(BODY_OPTION, "Comments", TextInputStyle.PARAGRAPH)
					.setRequired(true)
					.setPlaceholder("Start a line with " + NO_SPOILER_STR + " to not mark as spoiler")
					.build();
			
			return Modal.create(COMPONENT_ID, "Time reaction create")
					.addComponents(
							ActionRow.of(episode),
							ActionRow.of(link),
							ActionRow.of(body)
					)
					.build();
		};
	}
}
