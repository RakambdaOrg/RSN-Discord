package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import java.util.concurrent.TimeUnit;

public class CleanConfigScheduledRunner implements ScheduledRunner{
	public CleanConfigScheduledRunner(){
		Log.getLogger(null).info("Creating cleaner runner");
	}
	
	@Override
	public void run(){
		Settings.clean();
	}
	
	@Override
	public long getDelay(){
		return 2;
	}
	
	@Override
	public long getPeriod(){
		return 60;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
}
