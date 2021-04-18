package fr.raksrinana.rsndiscord.schedule.handler;

import fr.raksrinana.rsndiscord.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.settings.guild.schedule.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.schedule.ScheduleTag.DELETE_MESSAGE;

@ScheduleHandler
public class DeleteMessageScheduleHandler implements IScheduleHandler{
	public static final String MESSAGE_ID_KEY = "messageId";
	
	@Override
	public boolean acceptTag(@NotNull ScheduleTag tag){
		return Objects.equals(tag, DELETE_MESSAGE);
	}
	
	@Override
	public boolean accept(@NotNull ScheduleConfiguration reminder){
		Optional.ofNullable(reminder.getData().get(DeleteMessageScheduleHandler.MESSAGE_ID_KEY))
				.map(Long::parseLong)
				.ifPresent(messageId -> reminder.getChannel().getChannel()
						.map(channel -> JDAWrappers.delete(channel, messageId).submit()));
		return true;
	}
	
	@Override
	public int getPriority(){
		return 50;
	}
}
