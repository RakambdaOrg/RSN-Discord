package fr.raksrinana.rsndiscord.modules.schedule.config;

import fr.raksrinana.rsndiscord.modules.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.modules.schedule.handler.UnbanUserScheduleHandler;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.time.ZonedDateTime;
import java.util.Map;

public class UnbanScheduleConfiguration extends ScheduleConfiguration{
	public UnbanScheduleConfiguration(@NonNull User user, @NonNull TextChannel channel, @NonNull ZonedDateTime scheduleDate, @NonNull String message, @NonNull String userId){
		super(user, channel, scheduleDate, message, ScheduleTag.UNBAN_USER, Map.of(UnbanUserScheduleHandler.USER_ID_KEY, userId));
	}
}
