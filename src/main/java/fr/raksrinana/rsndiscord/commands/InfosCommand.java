package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@BotCommand
public class InfosCommand extends BasicCommand{
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		Actions.reply(event, "", Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Bot infos", null).addField("Bot version", Main.getRSNBotVersion(), false).addField("Last start (local time):", Main.bootTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), false).addField("Time elapsed", Duration.between(Main.bootTime, ZonedDateTime.now()).toString(), false).build());
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Bot infos";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("info");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Sends infos about the bot";
	}
}
