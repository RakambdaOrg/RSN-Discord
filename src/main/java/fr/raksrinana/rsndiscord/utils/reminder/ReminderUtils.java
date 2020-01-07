package fr.raksrinana.rsndiscord.utils.reminder;

import fr.raksrinana.rsndiscord.commands.reminder.ReminderCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.ReminderConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.SortedList;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import java.text.MessageFormat;
import java.util.Collection;

public class ReminderUtils{
	private static final Collection<ReminderHandler> handlers = new SortedList<>();
	
	/**
	 * Add a reminder into the configuration and send a notification message with a countdown.
	 *
	 * @param reminder The reminder to add.
	 * @param channel  The channel to send the message to.
	 */
	public static void addReminderAndNotify(@NonNull ReminderConfiguration reminder, @NonNull TextChannel channel){
		addReminder(reminder, channel.getGuild());
		Actions.sendMessage(channel, MessageFormat.format("Reminder added for the {0}", reminder.getNotifyDate().format(Utilities.DATE_TIME_MINUTE_FORMATTER)), ReminderCommand.getEmbedFor(reminder)).thenAccept(message -> reminder.setReminderCountdownMessage(new MessageConfiguration(message)));
	}
	
	/**
	 * Add a reminder into the configuration.
	 *
	 * @param reminder The reminder to add.
	 * @param guild    The guild where it is from.
	 */
	public static void addReminder(@NonNull ReminderConfiguration reminder, @NonNull Guild guild){
		Settings.get(guild).addReminder(reminder);
	}
	
	public static void addHandler(@NonNull ReminderHandler handler){
		handlers.add(handler);
	}
	
	public static Collection<ReminderHandler> getHandlers(){
		return handlers;
	}
}
