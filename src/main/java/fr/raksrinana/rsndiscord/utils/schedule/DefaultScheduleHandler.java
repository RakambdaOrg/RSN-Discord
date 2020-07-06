package fr.raksrinana.rsndiscord.utils.schedule;

import fr.raksrinana.rsndiscord.settings.guild.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class DefaultScheduleHandler implements ScheduleHandler{
	@Override
	public boolean acceptTag(@NonNull ScheduleTag tag){
		return true;
	}
	
	@Override
	public boolean accept(@NonNull ScheduleConfiguration reminder){
		return reminder.getUser().getUser().flatMap(user -> reminder.getChannel().getChannel().map(channel -> {
			Actions.sendMessage(channel, translate(channel.getGuild(), "schedule.reminder-added", user.getAsMention(), reminder.getMessage()), null);
			Optional.ofNullable(reminder.getReminderCountdownMessage()).flatMap(MessageConfiguration::getMessage).ifPresent(Actions::deleteMessage);
			return true;
		})).orElse(false);
	}
	
	@Override
	public int getPriority(){
		return Integer.MAX_VALUE;
	}
}
