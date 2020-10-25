package fr.raksrinana.rsndiscord.modules.schedule.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import fr.raksrinana.rsndiscord.modules.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.modules.schedule.handler.DeleteMessageScheduleHandler;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class DeleteMessageScheduleConfiguration extends ScheduleConfiguration{
	public DeleteMessageScheduleConfiguration(@NonNull User user, @NonNull ZonedDateTime scheduleDate, @NonNull Message message){
		super(user, message.getTextChannel(), scheduleDate, MessageFormat.format("Delete message {0}", message.getId()), ScheduleTag.DELETE_MESSAGE, Map.of(DeleteMessageScheduleHandler.MESSAGE_ID_KEY, message.getId()));
	}
}
