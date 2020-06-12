package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class TimeCommand extends BasicCommand{
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		Actions.reply(event, "", Utilities.buildEmbed(event.getAuthor(), Color.GREEN, translate(event.getGuild(), "time.title"), null).addField(translate(event.getGuild(), "time.time"), ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME), false).addField(translate(event.getGuild(), "time.milliseconds"), String.valueOf(System.currentTimeMillis()), false).build());
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.time.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("time");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.time.description");
	}
}
