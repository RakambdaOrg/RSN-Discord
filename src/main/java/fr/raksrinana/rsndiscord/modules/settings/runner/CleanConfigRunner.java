package fr.raksrinana.rsndiscord.modules.settings.runner;

import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
public class CleanConfigRunner implements IScheduledRunner{
	private final JDA jda;
	
	public CleanConfigRunner(JDA jda){
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
		return MINUTES;
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
