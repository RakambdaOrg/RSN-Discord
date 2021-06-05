package fr.raksrinana.rsndiscord.scheduleaction;

import fr.raksrinana.rsndiscord.scheduleaction.impl.DeleteMessageScheduleActionHandler;
import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

public class ScheduleActionService{
	public static void deleteMessage(@NotNull Message message, @NotNull Function<ZonedDateTime, ZonedDateTime> applyDelay){
		var schedule = new DeleteMessageScheduleActionHandler(message.getChannel().getIdLong(), message.getIdLong(), applyDelay.apply(ZonedDateTime.now()));
		Settings.get(message.getGuild()).add(schedule);
	}
	
	@NotNull
	public static Consumer<Message> deleteMessage(@NotNull Function<ZonedDateTime, ZonedDateTime> applyDelay){
		return message -> deleteMessage(message, applyDelay);
	}
	
	@NotNull
	public static Consumer<Message> deleteMessageMins(int minutes){
		return deleteMessage(date -> date.plusMinutes(minutes));
	}
}
