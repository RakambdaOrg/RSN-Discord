package fr.raksrinana.rsndiscord.settings.guild.schedule;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.time.ZonedDateTime;
import java.util.Map;
import static fr.raksrinana.rsndiscord.schedule.ScheduleTag.UNBAN_USER;
import static fr.raksrinana.rsndiscord.schedule.handler.UnbanUserScheduleHandler.USER_ID_KEY;

public class UnbanScheduleConfiguration extends ScheduleConfiguration{
	public UnbanScheduleConfiguration(@NonNull User user, @NonNull TextChannel channel, @NonNull ZonedDateTime scheduleDate, @NonNull String message, @NonNull String userId){
		super(user, channel, scheduleDate, message, UNBAN_USER, Map.of(USER_ID_KEY, userId));
	}
}
