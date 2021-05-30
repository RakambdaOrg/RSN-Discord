package fr.raksrinana.rsndiscord.settings.guild.schedule;

import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.Map;
import static fr.raksrinana.rsndiscord.schedule.ScheduleTag.UNBAN_USER;
import static fr.raksrinana.rsndiscord.schedule.handler.UnbanUserScheduleHandler.USER_ID_KEY;

public class UnbanScheduleConfiguration extends ScheduleConfiguration{
	public UnbanScheduleConfiguration(@NotNull User user, @NotNull ZonedDateTime scheduleDate, @NotNull String message, @NotNull String userId){
		super(user, null, scheduleDate, message, UNBAN_USER, Map.of(USER_ID_KEY, userId));
	}
}
