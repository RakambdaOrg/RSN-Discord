package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@BotCommand
public class AnimeReactionCommand extends BasicCommand{
	private static final String COMMENT_STR = "--";
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		try{
			final var lines = new LinkedList<>(Arrays.asList(String.join(" ", args).strip().split("\n")));
			if(!lines.isEmpty()){
				var newText = "__**EP " + lines.pop() + "**__";
				if(!lines.isEmpty()){
					newText += "\n" + lines.stream().map(s -> {
						var decorator = "||";
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
				Actions.reply(event, newText, null);
			}
			return CommandResult.SUCCESS;
		}
		catch(Exception e){
			Log.getLogger(event.getGuild()).error("Failed to parse anime reaction", e);
			Actions.reply(event, "Failed to parse text", null);
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
	public String getName(){
		return "Anime reaction";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("animereaction", "ar");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Formats anime reactions";
	}
}
