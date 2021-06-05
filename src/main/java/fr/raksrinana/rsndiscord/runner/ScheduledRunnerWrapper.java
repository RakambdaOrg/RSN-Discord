package fr.raksrinana.rsndiscord.runner;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

@Getter
@Log4j2
public class ScheduledRunnerWrapper implements Runnable{
	private final IScheduledRunner runner;
	
	public ScheduledRunnerWrapper(@NotNull IScheduledRunner runner){
		this.runner = runner;
		log.info("Created {} scheduler runner", runner.getName());
	}
	
	@Override
	public void run(){
		log.info("Starting {} scheduler runner", getRunner().getName());
		try{
			getRunner().execute();
		}
		catch(Exception e){
			log.error("Error while executing {} scheduled runner", getRunner().getName(), e);
		}
		log.info("{} scheduled runner done", getRunner().getName());
	}
}
