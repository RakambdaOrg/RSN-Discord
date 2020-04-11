package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@BotCommand
public class TimeCommand extends BasicCommand{
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		Actions.reply(event, "", Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Server time infos", null).addField("Time:", ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME), false).addField("Ms", String.valueOf(System.currentTimeMillis()), false).build());
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Get server time";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("time");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Get the current time of the server";
	}
}
