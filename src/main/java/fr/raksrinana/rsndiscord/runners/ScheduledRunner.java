package fr.raksrinana.rsndiscord.runners;

import lombok.NonNull;
import java.util.concurrent.TimeUnit;

public interface ScheduledRunner{
	void execute() throws Exception;
	
	long getDelay();
	
	@NonNull String getName();
	
	long getPeriod();
	
	@NonNull TimeUnit getPeriodUnit();
}
