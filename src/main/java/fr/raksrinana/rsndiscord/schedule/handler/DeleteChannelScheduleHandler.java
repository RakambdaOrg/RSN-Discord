package fr.raksrinana.rsndiscord.schedule.handler;

import fr.raksrinana.rsndiscord.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.settings.guild.schedule.ScheduleConfiguration;
import lombok.NonNull;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.schedule.ScheduleTag.DELETE_CHANNEL;

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
