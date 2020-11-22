package fr.raksrinana.rsndiscord.modules.schedule.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.Map;
import static fr.raksrinana.rsndiscord.modules.schedule.ScheduleTag.DELETE_MESSAGE;
import static fr.raksrinana.rsndiscord.modules.schedule.handler.DeleteMessageScheduleHandler.MESSAGE_ID_KEY;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class DeleteMessageScheduleConfiguration extends ScheduleConfiguration{
	public DeleteMessageScheduleConfiguration(@NonNull User user, @NonNull ZonedDateTime scheduleDate, @NonNull Message message){
		super(user, message.getTextChannel(), scheduleDate, MessageFormat.format("Delete message {0}", message.getId()), DELETE_MESSAGE,
				Map.of(MESSAGE_ID_KEY, message.getId()));
	}
	
	public DeleteMessageScheduleConfiguration(@NonNull User user, @NonNull ZonedDateTime scheduleDate, @NonNull TextChannel channel, long messageId){
		super(user, channel, scheduleDate, MessageFormat.format("Delete message {0}", messageId), DELETE_MESSAGE,
				Map.of(MESSAGE_ID_KEY, Long.toString(messageId)));
	}
}
