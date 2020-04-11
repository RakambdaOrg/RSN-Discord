package fr.raksrinana.rsndiscord.settings.guild.schedule;

import fr.raksrinana.rsndiscord.settings.guild.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleTag;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.time.ZonedDateTime;

public class SimpleScheduleConfiguration extends ScheduleConfiguration{
	public SimpleScheduleConfiguration(@NonNull User user, @NonNull TextChannel channel, @NonNull ZonedDateTime scheduleDate, @NonNull String message){
		super(user, channel, scheduleDate, message);
	}
	
	public SimpleScheduleConfiguration(@NonNull User user, @NonNull TextChannel channel, @NonNull ZonedDateTime scheduleDate, @NonNull String message, @NonNull ScheduleTag tag){
		super(user, channel, scheduleDate, message, tag);
	}
}
