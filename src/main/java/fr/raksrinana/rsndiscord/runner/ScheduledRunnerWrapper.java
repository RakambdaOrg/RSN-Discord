package fr.raksrinana.rsndiscord.runner;

import fr.raksrinana.rsndiscord.log.Log;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ScheduledRunnerWrapper implements Runnable{
	private final IScheduledRunner runner;
	
	public ScheduledRunnerWrapper(@NotNull IScheduledRunner runner){
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
