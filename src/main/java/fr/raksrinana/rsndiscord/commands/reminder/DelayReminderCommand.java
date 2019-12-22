package fr.raksrinana.rsndiscord.commands.reminder;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DelayReminderCommand extends ReminderCommand{
	private static final Pattern PERIOD_PATTERN = Pattern.compile("([0-9]+)([mhd])");
	
	public DelayReminderCommand(Command parent){
		super(parent);
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("delay", "The delay of when to do the reminder in the format XdXhXm where Xs are numbers", false);
		builder.addField("message", "The message of the reminder", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.size() < 2){
			return CommandResult.BAD_ARGUMENTS;
		}
		final var delay = parsePeriod(args.pop());
		final var notifyDate = LocalDateTime.now().plus(delay);
		addReminder(event, notifyDate, String.join(" ", args));
		return CommandResult.SUCCESS;
	}
	
	public static Duration parsePeriod(@NonNull String period){
		period = period.toLowerCase(Locale.ENGLISH);
		Matcher matcher = PERIOD_PATTERN.matcher(period);
		Duration duration = Duration.ZERO;
		while(matcher.find()){
			int amount = Integer.parseInt(matcher.group(1));
			String type = matcher.group(2);
			switch(type){
				case "m":
					duration = duration.plus(Duration.ofMinutes(amount));
					break;
				case "h":
					duration = duration.plus(Duration.ofHours(amount));
					break;
				case "d":
					duration = duration.plus(Duration.ofDays(amount));
					break;
			}
		}
		return duration;
	}
	
	@Override
	public @NonNull String getCommandUsage(){
		return super.getCommandUsage() + " <delay> <message...>";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Adds a reminder";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("delay");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Adds a reminder to be notified later after some delay";
	}
}
