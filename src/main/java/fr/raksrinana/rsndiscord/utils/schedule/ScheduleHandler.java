package fr.raksrinana.rsndiscord.utils.schedule;

import fr.raksrinana.rsndiscord.settings.guild.ScheduleConfiguration;
import lombok.NonNull;

public interface ScheduleHandler extends Comparable<ScheduleHandler>{
	boolean acceptTag(@NonNull ScheduleTag tag);
	
	boolean accept(@NonNull ScheduleConfiguration reminder);
	
	@Override
	default int compareTo(@NonNull ScheduleHandler o){
		return Integer.compare(getPriority(), o.getPriority());
	}
	
	/**
	 * The lowest will be executed first.
	 *
	 * @return The priority.
	 */
	int getPriority();
}
