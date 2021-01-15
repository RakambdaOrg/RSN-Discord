package fr.raksrinana.rsndiscord.schedule.handler;

import fr.raksrinana.rsndiscord.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.settings.guild.schedule.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import lombok.NonNull;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Optional.ofNullable;

@ScheduleHandler
public class DefaultScheduleHandler implements IScheduleHandler{
	@Override
	public boolean acceptTag(@NonNull ScheduleTag tag){
		return true;
	}
	
	@Override
	public boolean accept(@NonNull ScheduleConfiguration reminder){
		return reminder.getUser().getUser().flatMap(user -> reminder.getChannel().getChannel().map(channel -> {
			channel.sendMessage(translate(channel.getGuild(), "schedule.reminder-added", user.getAsMention(), reminder.getMessage())).submit();
			ofNullable(reminder.getReminderCountdownMessage())
					.flatMap(MessageConfiguration::getMessage)
					.ifPresent(message -> message.delete().submit());
			return true;
		})).orElse(false);
	}
	
	@Override
	public int getPriority(){
		return Integer.MAX_VALUE;
	}
}
