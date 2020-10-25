package fr.raksrinana.rsndiscord.modules.schedule.handler;

import fr.raksrinana.rsndiscord.modules.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.modules.schedule.config.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@ScheduleHandler
public class DefaultScheduleHandler implements IScheduleHandler{
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
