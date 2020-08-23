package fr.raksrinana.rsndiscord.runners.config;

import fr.raksrinana.rsndiscord.runners.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import java.util.concurrent.TimeUnit;

public class CleanConfigScheduledRunner implements ScheduledRunner{
	private final JDA jda;
	
	public CleanConfigScheduledRunner(JDA jda){
		this.jda = jda;
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
	
	@Override
	public void execute(){
		Settings.clean(jda);
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Config cleaner";
	}
}
