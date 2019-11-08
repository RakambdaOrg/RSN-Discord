package fr.raksrinana.rsndiscord.runners;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-24.
 *
 * @author Thomas Couchoud
 * @since 2018-10-24
 */
public interface ScheduledRunner extends Runnable{
	long getDelay();
	
	long getPeriod();
	
	@Nonnull
	TimeUnit getPeriodUnit();
}
