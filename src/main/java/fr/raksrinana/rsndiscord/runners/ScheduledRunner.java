package fr.raksrinana.rsndiscord.runners;

import lombok.NonNull;
import java.util.concurrent.TimeUnit;

public interface ScheduledRunner extends Runnable{
	long getDelay();
	
	long getPeriod();
	
	@NonNull TimeUnit getPeriodUnit();
}
