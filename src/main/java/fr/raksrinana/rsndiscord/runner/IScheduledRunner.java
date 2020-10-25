package fr.raksrinana.rsndiscord.runner;

import lombok.NonNull;
import java.util.concurrent.TimeUnit;

public interface IScheduledRunner{
	void execute() throws Exception;
	
	long getDelay();
	
	@NonNull String getName();
	
	long getPeriod();
	
	@NonNull TimeUnit getPeriodUnit();
}
