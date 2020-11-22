package fr.raksrinana.rsndiscord.modules.schedule.handler;

import fr.raksrinana.rsndiscord.modules.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.modules.schedule.config.ScheduleConfiguration;
import lombok.NonNull;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.modules.schedule.ScheduleTag.DELETE_CHANNEL;

@ScheduleHandler
public class DeleteChannelScheduleHandler implements IScheduleHandler{
	@Override
	public boolean acceptTag(@NonNull ScheduleTag tag){
		return Objects.equals(tag, DELETE_CHANNEL);
	}
	
	@Override
	public boolean accept(@NonNull ScheduleConfiguration reminder){
		reminder.getChannel().getChannel().map(channel -> channel.delete().submit());
		return true;
	}
	
	@Override
	public int getPriority(){
		return 50;
	}
}
