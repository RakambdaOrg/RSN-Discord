package fr.raksrinana.rsndiscord.runners.config;

import fr.raksrinana.rsndiscord.runners.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import java.util.concurrent.TimeUnit;

public class SaveConfigScheduledRunner implements ScheduledRunner{
	public SaveConfigScheduledRunner(@NonNull JDA jda){
	}
	
	@Override
	public void execute(){
		Settings.close();
	}
	
	@NonNull
	@Override
	public String getName(){
		return "config saver";
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
