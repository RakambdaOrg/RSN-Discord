package fr.raksrinana.rsndiscord.runners;

import lombok.NonNull;
import java.util.concurrent.TimeUnit;

public interface ScheduledRunner{
	long getDelay();
	
	long getPeriod();
	
	@NonNull TimeUnit getPeriodUnit();
	
	void execute() throws Exception;
	
	@NonNull String getName();
}
