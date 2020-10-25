package fr.raksrinana.rsndiscord.modules.schedule.handler;

import fr.raksrinana.rsndiscord.modules.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.modules.schedule.config.ScheduleConfiguration;
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
