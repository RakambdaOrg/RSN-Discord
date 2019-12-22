package fr.raksrinana.rsndiscord.commands.reminder;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ReminderConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class ReminderCommand extends BasicCommand{
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	
	public ReminderCommand(Command parent){
		super(parent);
	}
	
	protected void addReminder(@NonNull final GuildMessageReceivedEvent event, @NonNull LocalDateTime notifyDate, @NonNull String messageStr){
		final var reminder = new ReminderConfiguration(event.getAuthor(), event.getChannel(), notifyDate, messageStr);
		Settings.get(event.getGuild()).addReminder(reminder);
		Actions.reply(event, MessageFormat.format("Reminder added for the {0}", notifyDate.format(DATE_FORMATTER)), getEmbedFor(reminder)).thenAccept(message -> reminder.setReminderCountdownMessage(new MessageConfiguration(message)));
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
