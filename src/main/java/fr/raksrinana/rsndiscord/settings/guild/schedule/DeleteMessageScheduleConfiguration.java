package fr.raksrinana.rsndiscord.settings.guild.schedule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.Map;
import static fr.raksrinana.rsndiscord.schedule.ScheduleTag.DELETE_MESSAGE;
import static fr.raksrinana.rsndiscord.schedule.handler.DeleteMessageScheduleHandler.MESSAGE_ID_KEY;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class DeleteMessageScheduleConfiguration extends ScheduleConfiguration{
	public DeleteMessageScheduleConfiguration(@NotNull User user, @NotNull ZonedDateTime scheduleDate, @NotNull Message message){
		super(user, message.getTextChannel(), scheduleDate, MessageFormat.format("Delete message {0}", message.getId()), DELETE_MESSAGE,
				Map.of(MESSAGE_ID_KEY, message.getId()));
	}
	
	public DeleteMessageScheduleConfiguration(@NotNull User user, @NotNull ZonedDateTime scheduleDate, @NotNull TextChannel channel, long messageId){
		super(user, channel, scheduleDate, MessageFormat.format("Delete message {0}", messageId), DELETE_MESSAGE,
				Map.of(MESSAGE_ID_KEY, Long.toString(messageId)));
	}
}
