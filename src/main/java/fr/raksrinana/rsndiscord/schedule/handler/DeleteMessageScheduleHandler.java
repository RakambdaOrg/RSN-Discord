package fr.raksrinana.rsndiscord.schedule.handler;

import fr.raksrinana.rsndiscord.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.settings.guild.schedule.ScheduleConfiguration;
import lombok.NonNull;
import java.util.Objects;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.schedule.ScheduleTag.DELETE_MESSAGE;

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
