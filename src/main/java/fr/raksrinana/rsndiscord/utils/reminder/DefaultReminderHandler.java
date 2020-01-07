package fr.raksrinana.rsndiscord.utils.reminder;

import fr.raksrinana.rsndiscord.settings.guild.ReminderConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import java.text.MessageFormat;
import java.util.Optional;

public class DefaultReminderHandler implements ReminderHandler{
	@Override
	public boolean acceptTag(@NonNull ReminderTag tag){
		return true;
	}
	
	@Override
	public boolean accept(@NonNull ReminderConfiguration reminder){
		return reminder.getUser().getUser().flatMap(user -> reminder.getChannel().getChannel().map(channel -> {
			Actions.sendMessage(channel, MessageFormat.format("Reminder for {0}: {1}", user.getAsMention(), reminder.getMessage()), null);
			Optional.ofNullable(reminder.getReminderCountdownMessage()).flatMap(MessageConfiguration::getMessage).ifPresent(Actions::deleteMessage);
			return true;
		})).orElse(false);
	}
	
	@Override
	public int getPriority(){
		return Integer.MAX_VALUE;
	}
}
