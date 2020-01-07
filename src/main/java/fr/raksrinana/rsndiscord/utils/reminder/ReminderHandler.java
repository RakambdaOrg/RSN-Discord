package fr.raksrinana.rsndiscord.utils.reminder;

import fr.raksrinana.rsndiscord.settings.guild.ReminderConfiguration;
import lombok.NonNull;

public interface ReminderHandler extends Comparable<ReminderHandler>{
	boolean acceptTag(@NonNull ReminderTag tag);
	
	boolean accept(@NonNull ReminderConfiguration reminder);
	
	@Override
	default int compareTo(@NonNull ReminderHandler o){
		return Integer.compare(getPriority(), o.getPriority());
	}
	
	/**
	 * The lowest will be executed first.
	 *
	 * @return The priority.
	 */
	int getPriority();
}
