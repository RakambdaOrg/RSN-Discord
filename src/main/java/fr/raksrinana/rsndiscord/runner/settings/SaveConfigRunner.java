package fr.raksrinana.rsndiscord.runner.settings;

import fr.raksrinana.rsndiscord.runner.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
public class SaveConfigRunner implements IScheduledRunner{
	public SaveConfigRunner(@NotNull JDA jda){
	}
	
	@Override
	public long getDelay(){
		return 2;
	}
	
	@Override
	public long getPeriod(){
		return 5;
	}
	
	@NotNull
	@Override
	public String getName(){
		return "Config saver";
	}
	
	@Override
	public void execute(){
		Settings.close();
	}
	
	@NotNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
}
