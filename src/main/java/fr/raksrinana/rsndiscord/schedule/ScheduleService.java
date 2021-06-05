package fr.raksrinana.rsndiscord.schedule;

import fr.raksrinana.rsndiscord.schedule.impl.DeleteMessageScheduleHandler;
import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.function.Function;

public class ScheduleService{
	public static void deleteMessage(@NotNull Message message, @NotNull Function<ZonedDateTime, ZonedDateTime> applyDelay){
		var schedule = new DeleteMessageScheduleHandler(message.getChannel().getIdLong(), message.getIdLong(), applyDelay.apply(ZonedDateTime.now()));
		Settings.get(message.getGuild()).add(schedule);
	}
	
	@NotNull
	public static Function<Message, Message> deleteMessage(@NotNull Function<ZonedDateTime, ZonedDateTime> applyDelay){
		return message -> {
			deleteMessage(message, applyDelay);
			return message;
		};
	}
	
	@NotNull
	public static Function<Message, Message> deleteMessageMins(int minutes){
		return deleteMessage(date -> date.plusMinutes(minutes));
	}
}
