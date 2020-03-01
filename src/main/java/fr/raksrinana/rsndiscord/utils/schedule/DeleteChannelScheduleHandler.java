package fr.raksrinana.rsndiscord.utils.schedule;

import fr.raksrinana.rsndiscord.settings.guild.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import java.util.Objects;

public class DeleteChannelScheduleHandler implements ScheduleHandler{
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
