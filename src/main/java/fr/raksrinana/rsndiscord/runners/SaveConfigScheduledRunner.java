package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import java.util.concurrent.TimeUnit;

public class SaveConfigScheduledRunner implements ScheduledRunner{
	public SaveConfigScheduledRunner(){
		Log.getLogger(null).info("Creating saver runner");
	}
	
	@Override
	public void run(){
		Settings.close();
	}
	
	@Override
	public long getDelay(){
		return 2;
	}
	
	@Override
	public long getPeriod(){
		return 5;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
}
