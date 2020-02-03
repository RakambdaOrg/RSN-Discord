package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ScheduledRunnerRunnable implements Runnable{
	private final ScheduledRunner runner;
	
	public ScheduledRunnerRunnable(@NonNull ScheduledRunner runner){
		this.runner = runner;
		Log.getLogger(null).info("Created {} scheduler runner", runner.getName());
	}
	
	@Override
	public void run(){
		Log.getLogger(null).info("Starting {} scheduler runner", getRunner().getName());
		try{
			getRunner().execute();
		}
		catch(Exception e){
			Log.getLogger(null).error("Error while executing {} scheduled runner", getRunner().getName(), e);
		}
		Log.getLogger(null).info("{} scheduled runner done", getRunner().getName());
	}
}
