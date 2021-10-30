package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.components.impl.button.ArchiveMediaReactionButtonHandler;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.BotSlashCommand;
import fr.raksrinana.rsndiscord.command.base.SimpleCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.command.CommandResult.FAILED;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.lang.Character.isDigit;
import static net.dv8tion.jda.api.interactions.commands.OptionType.BOOLEAN;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@BotSlashCommand
@Log4j2
public class MediaReactionCommand extends SimpleCommand{
	private static final String COMMENT_STR = "--";
	private static final String CONTENT_OPTION_ID = "content";
	private static final String EPISODE_OPTION_ID = "episode";
	private static final String LINK_OPTION_ID = "link";
	private static final String ARCHIVE_OPTION_ID = "archive";
	
	@Override
	@NotNull
	public String getId(){
		return "time-reaction";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Media reaction";
	}
	
	@Override
	public boolean replyEphemeral(){
		return false;
	}
	
	@Override
	@NotNull
	protected Collection<? extends OptionData> getOptions(){
		return List.of(
				new OptionData(STRING, CONTENT_OPTION_ID, "Content").setRequired(true),
				new OptionData(STRING, EPISODE_OPTION_ID, "Episode"),
				new OptionData(STRING, LINK_OPTION_ID, "Link"),
				new OptionData(BOOLEAN, ARCHIVE_OPTION_ID, "Archive"));
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandEvent event, @NotNull Guild guild, @NotNull Member member){
		var content = event.getOption(CONTENT_OPTION_ID).getAsString();
		var episode = Optional.ofNullable(event.getOption(EPISODE_OPTION_ID)).map(OptionMapping::getAsString);
		var link = Optional.ofNullable(event.getOption(LINK_OPTION_ID)).map(OptionMapping::getAsString);
		var archive = Optional.ofNullable(event.getOption(ARCHIVE_OPTION_ID)).map(OptionMapping::getAsBoolean).orElse(false);
		
		try{
			var reactions = Arrays.stream(content.split("§")).collect(Collectors.toCollection(LinkedList::new));
			var messageContent = new StringBuilder();
			
			if(episode.isPresent() || link.isPresent()){
				episode.map(v -> "__**EP " + v + "**__").ifPresent(messageContent::append);
				link.ifPresent(l -> messageContent.append("\n").append(l));
			}
			
			reactions.stream()
					.filter(reaction -> !reaction.isBlank())
					.forEach(reaction -> {
						var decorator = "||";
						
						if(reaction.startsWith(COMMENT_STR)){
							reaction = reaction.substring(COMMENT_STR.length());
							decorator = "";
						}
						
						messageContent.append("\n");
						
						var parts = reaction.trim().split(" ", 2);
						if(parts.length < 2){
							messageContent.append(reaction);
						}
						else{
							var time = convertTime(parts[0]);
							messageContent.append(time).append(decorator).append(parts[1]).append(decorator);
						}
					});
			
			var messageAction = JDAWrappers.edit(event, messageContent.toString());
			
			if(archive){
				var buttonHandler = new ArchiveMediaReactionButtonHandler();
				messageAction = messageAction.addActionRow(buttonHandler.asComponent());
			}
			
			messageAction.submit()
					.thenAccept(message -> Settings.get(guild).getMediaReactionMessages().add(new MessageConfiguration(message)))
					.exceptionally(error -> {
						JDAWrappers.edit(event, "Failed to send message: " + error.getMessage()).submit();
						return null;
					});
			return HANDLED;
		}
		catch(Exception e){
			log.error("Failed to parse anime reaction", e);
			JDAWrappers.edit(event, translate(guild, "media-reaction.parse-error")).submit();
		}
		return FAILED;
	}
	
	@NotNull
	private static String convertTime(@NotNull String string) throws IllegalArgumentException{
		if(string.isBlank() || string.chars().anyMatch(c -> !isDigit(c))){
			return "N/A ";
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
}
