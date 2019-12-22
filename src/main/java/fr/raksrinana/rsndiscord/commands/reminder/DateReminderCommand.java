package fr.raksrinana.rsndiscord.commands.reminder;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class DateReminderCommand extends ReminderCommand{
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	
	public DateReminderCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("date", "The date of when to do the reminder in the format YYYY-MM-DD HH:MM", false);
		builder.addField("message", "The message of the reminder", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.size() < 3){
			return CommandResult.BAD_ARGUMENTS;
		}
		final var notifyDate = LocalDateTime.parse(args.pop() + " " + args.pop(), DATE_FORMATTER);
		addReminder(event, notifyDate, String.join(" ", args));
		return CommandResult.SUCCESS;
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <date> <message...>";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Adds a reminder";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("date");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Adds a reminder to be notified later at a specified date";
	}
}
