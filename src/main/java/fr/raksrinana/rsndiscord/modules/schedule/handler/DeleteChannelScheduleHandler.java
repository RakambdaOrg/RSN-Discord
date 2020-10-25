package fr.raksrinana.rsndiscord.modules.schedule.handler;

import fr.raksrinana.rsndiscord.modules.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.modules.schedule.config.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import java.util.Objects;
@ScheduleHandler
public class DeleteChannelScheduleHandler implements IScheduleHandler{
	@Override
	public boolean acceptTag(@NonNull ScheduleTag tag){
		return Objects.equals(tag, ScheduleTag.DELETE_CHANNEL);
	}
	
	@Override
	public boolean accept(@NonNull ScheduleConfiguration reminder){
		reminder.getChannel().getChannel().map(Actions::deleteChannel);
		return true;
	}
	
	@Override
	public int getPriority(){
		return 50;
	}
}
