package fr.raksrinana.rsndiscord.runner;

import org.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;

public interface IScheduledRunner{
	void execute() throws Exception;
	
	long getDelay();
	
	@NotNull String getName();
	
	long getPeriod();
	
	@NotNull TimeUnit getPeriodUnit();
}
