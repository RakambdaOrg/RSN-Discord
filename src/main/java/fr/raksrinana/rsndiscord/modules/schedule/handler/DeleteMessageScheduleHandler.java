package fr.raksrinana.rsndiscord.modules.schedule.handler;

import fr.raksrinana.rsndiscord.modules.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.modules.schedule.config.ScheduleConfiguration;
import lombok.NonNull;
import java.util.Objects;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.modules.schedule.ScheduleTag.DELETE_MESSAGE;

@ScheduleHandler
public class DeleteMessageScheduleHandler implements IScheduleHandler{
	public static final String MESSAGE_ID_KEY = "messageId";
	
	@Override
	public boolean acceptTag(@NonNull ScheduleTag tag){
		return Objects.equals(tag, DELETE_MESSAGE);
	}
	
	@Override
	public boolean accept(@NonNull ScheduleConfiguration reminder){
		Optional.ofNullable(reminder.getData().get(DeleteMessageScheduleHandler.MESSAGE_ID_KEY))
				.map(Long::parseLong)
				.ifPresent(messageId -> reminder.getChannel().getChannel()
						.map(channel -> channel.deleteMessageById(messageId).submit()));
		return true;
	}
	
	@Override
	public int getPriority(){
		return 50;
	}
}
