package fr.raksrinana.rsndiscord.settings.guild.schedule;

import fr.raksrinana.rsndiscord.settings.guild.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.schedule.RemoveRoleScheduleHandler;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleTag;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.Map;

public class RemoveRoleScheduleConfiguration extends ScheduleConfiguration{
	public RemoveRoleScheduleConfiguration(@NonNull User user, @NonNull TextChannel channel, @NonNull ZonedDateTime scheduleDate, @NonNull Role role){
		super(user, channel, scheduleDate, MessageFormat.format("Remove role {0} for {1}", role, user), ScheduleTag.REMOVE_ROLE, Map.of(RemoveRoleScheduleHandler.ROLE_ID_KEY, role.getId()));
	}
}
