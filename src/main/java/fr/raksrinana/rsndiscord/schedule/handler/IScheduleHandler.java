package fr.raksrinana.rsndiscord.schedule.handler;

import fr.raksrinana.rsndiscord.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.settings.guild.schedule.ScheduleConfiguration;
import org.jetbrains.annotations.NotNull;

public interface IScheduleHandler extends Comparable<IScheduleHandler>{
	boolean acceptTag(@NotNull ScheduleTag tag);
	
	boolean accept(@NotNull ScheduleConfiguration reminder);
	
	@Override
	default int compareTo(@NotNull IScheduleHandler o){
		return Integer.compare(getPriority(), o.getPriority());
	}
	
	/**
	 * The lowest will be executed first.
	 *
	 * @return The priority.
	 */
	int getPriority();
}
