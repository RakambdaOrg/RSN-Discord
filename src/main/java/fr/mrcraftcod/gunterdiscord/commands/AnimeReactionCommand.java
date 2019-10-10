package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.BotCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
@BotCommand
public class AnimeReactionCommand extends BasicCommand{
	private static final String COMMENT_STR = "--";
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		try{
			final var lines = new LinkedList<>(Arrays.asList(String.join(" ", args).strip().split("\n")));
			if(!lines.isEmpty()){
				var newText = "__**EP " + lines.pop() + "**__";
				if(!lines.isEmpty()){
					newText += "\n" + lines.stream().map(s -> {
						if(s.isBlank()){
							return "";
						}
						if(s.startsWith(COMMENT_STR)){
							return s.substring(COMMENT_STR.length()).trim();
						}
						final var parts = new LinkedList<>(Arrays.asList(s.split(" ", 2)));
						if(parts.isEmpty()){
							return s;
						}
						return convertTime(parts) + "||" + String.join(" ", parts) + "||";
					}).collect(Collectors.joining("\n"));
				}
				Actions.reply(event, newText);
			}
			return CommandResult.SUCCESS;
		}
		catch(Exception e){
			Log.getLogger(event.getGuild()).error("Failed to parse anime reaction", e);
			Actions.reply(event, "Failed to parse text");
		}
		return CommandResult.FAILED;
	}
	
	private static String convertTime(LinkedList<String> stringList) throws IllegalArgumentException{
		if(stringList.size() < 1){
			return "";
		}
		final var originalStr = stringList.peek();
		if(originalStr.isBlank() || !Character.isDigit(originalStr.charAt(0))){
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
			return String.format("%02d:%02d:%02d ", Integer.parseInt(str.substring(0, cut)), Integer.parseInt(str.substring(str.length() - 2, cut)), Integer.parseInt(str.substring(str.length() - 2)));
		}
		catch(Exception e){
			throw new IllegalArgumentException("Failed to parse " + originalStr, e);
		}
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Anime reaction";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("animereaction", "ar");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Formats anime reactions";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
