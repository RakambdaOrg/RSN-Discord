package fr.raksrinana.rsndiscord.command2.impl;

import fr.raksrinana.rsndiscord.button.impl.ArchiveMediaReactionButtonHandler;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.BotSlashCommand;
import fr.raksrinana.rsndiscord.command2.base.SimpleCommand;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.command.CommandResult.FAILED;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.PACKAGE;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.lang.Character.isDigit;
import static net.dv8tion.jda.api.interactions.commands.OptionType.STRING;

@BotSlashCommand
public class MediaReactionCommand extends SimpleCommand{
	private static final String COMMENT_STR = "--";
	private static final String OPTIONS_OPTION_ID = "options";
	private static final String CONTENT_OPTION_ID = "content";
	
	@Override
	@NotNull
	public String getId(){
		return "media-reaction";
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
				new OptionData(STRING, OPTIONS_OPTION_ID, "Options"));
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		var options = Optional.ofNullable(event.getOption(OPTIONS_OPTION_ID)).map(OptionMapping::getAsString);
		var content = event.getOption(CONTENT_OPTION_ID).getAsString();
		
		try{
			var isMovie = options.map(o -> o.contains("m")).orElse(false);
			var askArchive = options.map(o -> o.contains("a")).orElse(false);
			var defaultDecorator = options.filter(o -> o.contains("c")).map(o -> "").orElse("||");
			
			var reactions = Arrays.stream(content.split("§")).collect(Collectors.toCollection(LinkedList::new));
			var messageContent = new StringBuilder();
			
			if(!isMovie){
				messageContent.append("__**EP ").append(reactions.pop()).append("**__");
			}
			
			reactions.stream()
					.filter(reaction -> !reaction.isBlank())
					.forEach(reaction -> {
						var decorator = defaultDecorator;
						
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
			
			if(askArchive){
				messageContent.append("\n\n").append(translate(guild, "media-reaction.archive", PACKAGE.getValue()));
			}
			
			var messageAction = JDAWrappers.edit(event, messageContent.toString());
			
			if(askArchive){
				var buttonHandler = new ArchiveMediaReactionButtonHandler();
				messageAction = messageAction.setActionRow(buttonHandler.asButton());
				Settings.get(guild).addButtonHandler(buttonHandler);
			}
			
			messageAction.submit()
					.thenAccept(message -> Settings.get(guild).getMediaReactionMessages().add(new MessageConfiguration(message)))
					.exceptionally(error -> {
						JDAWrappers.edit(event, "Failed to send message: " + error.getMessage()).submit();
						return null;
					});
			return SUCCESS;
		}
		catch(Exception e){
			Log.getLogger(guild).error("Failed to parse anime reaction", e);
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
