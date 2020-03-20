package fr.raksrinana.rsndiscord.runners.config;

import fr.raksrinana.rsndiscord.runners.ScheduledRunner;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import java.util.concurrent.TimeUnit;

public class CleanConfigScheduledRunner implements ScheduledRunner{
	public CleanConfigScheduledRunner(JDA jda){
	}
	
	@Override
	public void execute(){
		// Settings.clean();
	}
	
	@NonNull
	@Override
	public String getName(){
		return "config cleaner";
	}
	
	@Override
	public long getDelay(){
		return 2;
	}
	
	@Override
	public long getPeriod(){
		return 12 * 60;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
}
