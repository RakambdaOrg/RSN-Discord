package fr.raksrinana.rsndiscord.commands.reminder;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.settings.guild.ReminderConfiguration;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.reminder.ReminderUtils;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class ReminderCommand extends BasicCommand{
	public ReminderCommand(Command parent){
		super(parent);
	}
	
	public static MessageEmbed getEmbedFor(@NonNull ReminderConfiguration reminder){
		final var notifyDate = reminder.getNotifyDate();
		final var builder = Utilities.buildEmbed(reminder.getUser().getUser().orElse(null), Color.ORANGE, "Reminder", null);
		builder.addField("Date", notifyDate.format(Utilities.DATE_TIME_MINUTE_FORMATTER), true);
		builder.addField("Remaining time", Utilities.durationToString(Duration.between(LocalDateTime.now(), notifyDate)), true);
		builder.addField("Message", reminder.getMessage(), false);
		return builder.build();
	}
	
	protected void addReminder(@NonNull final GuildMessageReceivedEvent event, @NonNull LocalDateTime notifyDate, @NonNull String messageStr){
		final var reminder = new ReminderConfiguration(event.getAuthor(), event.getChannel(), notifyDate, messageStr);
		ReminderUtils.addReminderAndNotify(reminder, event.getChannel());
	}
}
