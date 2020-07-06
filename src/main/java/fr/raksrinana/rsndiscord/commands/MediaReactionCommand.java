package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.WaitingReactionMessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionTag;
import fr.raksrinana.rsndiscord.utils.reaction.ReactionUtils;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class MediaReactionCommand extends BasicCommand{
	private static final String COMMENT_STR = "--";
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		try{
			var hasEpisodes = true;
			var askArchive = false;
			final var lines = new LinkedList<>(Arrays.asList(String.join(" ", args).strip().split("\n")));
			if(!lines.isEmpty()){
				var defaultDecorator = "||";
				if(lines.peek().startsWith("o")){
					final var options = lines.pop();
					hasEpisodes = !options.contains("m");
					askArchive = options.contains("a");
					if(options.contains("c")){
						defaultDecorator = "";
					}
				}
				if(lines.isEmpty()){
					return CommandResult.BAD_ARGUMENTS;
				}
				var newText = "";
				if(hasEpisodes){
					newText += "__**EP " + lines.pop() + "**__";
				}
				if(!lines.isEmpty()){
					final var finalDefaultDecorator = defaultDecorator;
					newText += "\n" + lines.stream().map(s -> {
						var decorator = finalDefaultDecorator;
						if(s.isBlank()){
							return "";
						}
						if(s.startsWith(COMMENT_STR)){
							s = s.substring(COMMENT_STR.length()).trim();
							decorator = "";
						}
						final var parts = new LinkedList<>(Arrays.asList(s.split(" ", 2)));
						if(parts.isEmpty()){
							return s;
						}
						return convertTime(parts) + decorator + String.join(" ", parts) + decorator;
					}).collect(Collectors.joining("\n"));
				}
				if(askArchive){
					newText += "\n\n" + translate(event.getGuild(), "media-reaction.archive", BasicEmotes.PACKAGE.getValue());
				}
				final var restMessage = Actions.sendMessage(event.getChannel(), newText, null, true);
				if(askArchive){
					restMessage.thenAccept(message -> {
						Actions.addReaction(message, BasicEmotes.PACKAGE.getValue());
						Settings.get(event.getGuild()).addMessagesAwaitingReaction(new WaitingReactionMessageConfiguration(message, ReactionTag.MEDIA_REACTION, Map.of(ReactionUtils.DELETE_KEY, Boolean.toString(false))));
					});
				}
				return CommandResult.SUCCESS;
			}
			return CommandResult.BAD_ARGUMENTS;
		}
		catch(Exception e){
			Log.getLogger(event.getGuild()).error("Failed to parse anime reaction", e);
			Actions.reply(event, translate(event.getGuild(), "media-reaction.parse-error"), null);
		}
		return CommandResult.FAILED;
	}
	
	private static String convertTime(LinkedList<String> stringList) throws IllegalArgumentException{
		if(stringList.size() < 1){
			return "";
		}
		final var originalStr = stringList.peek();
		if(originalStr.isBlank() || originalStr.chars().anyMatch(c -> !Character.isDigit(c))){
			return "N/A ";
		}
		stringList.pop();
		try{
			final var str = originalStr.replace(":", "");
			if(str.length() <= 2){
				return String.format("00:%02d ", Integer.parseInt(str));
			}
			if(str.length() <= 4){
				final var cut = str.length() - 2;
				return String.format("%02d:%02d ", Integer.parseInt(str.substring(0, cut)), Integer.parseInt(str.substring(cut)));
			}
			final var cut = str.length() - 4;
			return String.format("%02d:%02d:%02d ", Integer.parseInt(str.substring(0, cut)), Integer.parseInt(str.substring(cut, str.length() - 2)), Integer.parseInt(str.substring(str.length() - 2)));
		}
		catch(Exception e){
			throw new IllegalArgumentException("Failed to parse " + originalStr, e);
		}
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.media-reaction.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("mediareaction", "mr");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.media-reaction.description");
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("option", translate(guild, "command.media-reaction.help.option"), false);
		builder.addField("text", translate(guild, "command.media-reaction.help.text"), false);
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + "[o<option>] <text>";
	}
}
