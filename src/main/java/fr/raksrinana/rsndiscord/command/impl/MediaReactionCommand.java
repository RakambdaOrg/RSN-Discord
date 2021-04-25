package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.DeleteMode;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.reaction.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import static fr.raksrinana.rsndiscord.command.CommandResult.*;
import static fr.raksrinana.rsndiscord.command.DeleteMode.NEVER;
import static fr.raksrinana.rsndiscord.reaction.ReactionTag.MEDIA_REACTION;
import static fr.raksrinana.rsndiscord.reaction.ReactionUtils.DELETE_KEY;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.PACKAGE;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.lang.Character.isDigit;
import static java.util.stream.Collectors.joining;

@BotCommand
public class MediaReactionCommand extends BasicCommand{
	private static final String COMMENT_STR = "--";
	
	@Override
	public void addHelp(@NotNull Guild guild, @NotNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("option", translate(guild, "command.media-reaction.help.option"), false)
				.addField("text", translate(guild, "command.media-reaction.help.text"), false);
	}
	
	@NotNull
	@Override
	public CommandResult execute(@NotNull GuildMessageReceivedEvent event, @NotNull LinkedList<String> args){
		super.execute(event, args);
		
		var guild = event.getGuild();
		
		try{
			var hasEpisodes = true;
			var askArchive = false;
			var lines = new LinkedList<>(Arrays.asList(String.join(" ", args).strip().split("\n")));
			if(!lines.isEmpty()){
				var defaultDecorator = "||";
				if(lines.peek().startsWith("o")){
					var options = lines.pop();
					hasEpisodes = !options.contains("m");
					askArchive = options.contains("a");
					if(options.contains("c")){
						defaultDecorator = "";
					}
				}
				if(lines.isEmpty()){
					return BAD_ARGUMENTS;
				}
				var newText = "";
				if(hasEpisodes){
					newText += "__**EP " + lines.pop() + "**__";
				}
				if(!lines.isEmpty()){
					var finalDefaultDecorator = defaultDecorator;
					newText += "\n" + lines.stream().map(s -> {
						var decorator = finalDefaultDecorator;
						if(s.isBlank()){
							return "";
						}
						if(s.startsWith(COMMENT_STR)){
							s = s.substring(COMMENT_STR.length()).trim();
							decorator = "";
						}
						var parts = new LinkedList<>(Arrays.asList(s.split(" ", 2)));
						if(parts.isEmpty()){
							return s;
						}
						return convertTime(parts) + decorator + String.join(" ", parts) + decorator;
					}).collect(joining("\n"));
				}
				if(askArchive){
					newText += "\n\n" + translate(guild, "media-reaction.archive", PACKAGE.getValue());
				}
				var restMessage = JDAWrappers.message(event, newText).submit();
				if(askArchive){
					restMessage = restMessage.thenApply(message -> {
						JDAWrappers.addReaction(message, PACKAGE).submit();
						
						var reactionMessageConfiguration = new WaitingReactionMessageConfiguration(message, MEDIA_REACTION,
								Map.of(DELETE_KEY, Boolean.toString(false)));
						Settings.get(guild).addMessagesAwaitingReaction(reactionMessageConfiguration);
						
						return message;
					});
				}
				
				restMessage
						.thenAccept(message -> {
							JDAWrappers.delete(event.getMessage()).submit();
							Settings.get(guild).getMediaReactionMessages().add(new MessageConfiguration(message));
						})
						.exceptionally(error -> {
							JDAWrappers.message(event, "Failed to send message: " + error.getMessage()).submit();
							return null;
						});
				return SUCCESS;
			}
			return BAD_ARGUMENTS;
		}
		catch(Exception e){
			Log.getLogger(guild).error("Failed to parse anime reaction", e);
			JDAWrappers.message(event, translate(guild, "media-reaction.parse-error")).submit();
		}
		return FAILED;
	}
	
	@NotNull
	private static String convertTime(@NotNull LinkedList<String> stringList) throws IllegalArgumentException{
		if(stringList.size() < 1){
			return "";
		}
		
		var originalStr = stringList.peek();
		if(originalStr.isBlank() || originalStr.chars().anyMatch(c -> !isDigit(c))){
			return "N/A ";
		}
		stringList.pop();
		
		try{
			var str = originalStr.replace(":", "");
			if(str.length() <= 2){
				return String.format("00:%02d ", Integer.parseInt(str));
			}
			if(str.length() <= 4){
				var cut = str.length() - 2;
				return String.format("%02d:%02d ", Integer.parseInt(str.substring(0, cut)), Integer.parseInt(str.substring(cut)));
			}
			var cut = str.length() - 4;
			return String.format("%02d:%02d:%02d ",
					Integer.parseInt(str.substring(0, cut)),
					Integer.parseInt(str.substring(cut, str.length() - 2)),
					Integer.parseInt(str.substring(str.length() - 2)));
		}
		catch(Exception e){
			throw new IllegalArgumentException("Failed to parse " + originalStr, e);
		}
	}
	
	@Override
	public @NotNull String getCommandUsage(){
		return super.getCommandUsage() + "[o<option>] <text>";
	}
	
	@Override
	public @NotNull DeleteMode getDeleteMode(){
		return NEVER;
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return new SimplePermission("command.media-reaction", false);
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return translate(guild, "command.media-reaction.name");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return translate(guild, "command.media-reaction.description");
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("mediareaction", "mr");
	}
}
