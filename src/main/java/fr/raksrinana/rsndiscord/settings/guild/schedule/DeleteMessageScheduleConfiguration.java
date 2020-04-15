package fr.raksrinana.rsndiscord.settings.guild.schedule;

import fr.raksrinana.rsndiscord.settings.guild.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.schedule.DeleteMessageScheduleHandler;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleTag;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.Map;

public class DeleteMessageScheduleConfiguration extends ScheduleConfiguration{
	public DeleteMessageScheduleConfiguration(@NonNull User user, @NonNull ZonedDateTime scheduleDate, @NonNull Message message){
		super(user, message.getTextChannel(), scheduleDate, MessageFormat.format("Delete message {0}", message.getId()), ScheduleTag.DELETE_MESSAGE, Map.of(DeleteMessageScheduleHandler.MESSAGE_ID_KEY, message.getId()));
	}
}
