package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@BotCommand
public class TempCommand extends BasicCommand{
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		final var date = LocalDateTime.parse(String.join(" ", args), Utilities.DATE_TIME_MINUTE_FORMATTER);
		Settings.get(event.getGuild()).getTraktConfiguration().setLastAccess(event.getAuthor(), "history", date);
		Actions.reply(event, "Set date to " + date.toString(), null);
		return CommandResult.SUCCESS;
	}
	
	@Override
	public @NonNull AccessLevel getAccessLevel(){
		return AccessLevel.CREATOR;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("temp");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "";
	}
}
