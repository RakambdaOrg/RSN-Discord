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
		final var now = ZonedDateTime.now();
		final var builder = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Bot infos", null);
		builder.addField("Bot version", Main.getRSNBotVersion(), false);
		builder.addField("Current time:", now.format(DateTimeFormatter.ISO_ZONED_DATE_TIME), false);
		builder.addField("Last start (local time):", Main.bootTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME), false);
		builder.addField("Time elapsed", Duration.between(Main.bootTime, now).toString(), false);
		Actions.reply(event, "", builder.build());
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
