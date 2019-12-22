package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ReminderConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@BotCommand
public class ReminderCommand extends BasicCommand{
	private static final Pattern PERIOD_PATTERN = Pattern.compile("([0-9]+)([mhd])");
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	
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
		final var reminder = new ReminderConfiguration(event.getAuthor(), event.getChannel(), notifyDate, String.join(" ", args));
		Settings.get(event.getGuild()).addReminder(reminder);
		Actions.reply(event, MessageFormat.format("Reminder added for the {0}", notifyDate.format(DATE_FORMATTER)), getEmbedFor(reminder)).thenAccept(message -> reminder.setReminderCountdownMessage(new MessageConfiguration(message)));
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
		return List.of("reminder");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Adds a reminder to be notified later";
	}
	
	public static MessageEmbed getEmbedFor(@NonNull ReminderConfiguration reminder){
		final var notifyDate = reminder.getNotifyDate();
		final var builder = Utilities.buildEmbed(reminder.getUser().getUser().orElse(null), Color.ORANGE, "Reminder", null);
		builder.addField("Date", notifyDate.format(DATE_FORMATTER), false);
		builder.addField("Remaining time", Utilities.durationToString(Duration.between(LocalDateTime.now(), notifyDate)), true);
		builder.addField("Message", reminder.getMessage(), true);
		return builder.build();
	}
}
