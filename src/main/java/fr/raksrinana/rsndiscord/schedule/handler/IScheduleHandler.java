package fr.raksrinana.rsndiscord.schedule.handler;

import fr.raksrinana.rsndiscord.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.settings.guild.schedule.ScheduleConfiguration;
import lombok.NonNull;

public interface IScheduleHandler extends Comparable<IScheduleHandler>{
	boolean acceptTag(@NonNull ScheduleTag tag);
	
	boolean accept(@NonNull ScheduleConfiguration reminder);
	
	@Override
	default int compareTo(@NonNull IScheduleHandler o){
		return Integer.compare(getPriority(), o.getPriority());
	}
	
	/**
	 * The lowest will be executed first.
	 *
	 * @return The priority.
	 */
	int getPriority();
}
