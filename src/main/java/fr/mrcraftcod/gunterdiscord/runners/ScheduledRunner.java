package fr.mrcraftcod.gunterdiscord.runners;

import java.util.concurrent.TimeUnit;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-24.
 *
 * @author Thomas Couchoud
 * @since 2018-10-24
 */
public interface ScheduledRunner extends Runnable{
	long getPeriod();
	
	TimeUnit getPeriodUnit();
}
