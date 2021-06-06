package fr.raksrinana.rsndiscord.runner.impl.settings;

import fr.raksrinana.rsndiscord.runner.api.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.api.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
public class SaveConfigRunner implements IScheduledRunner{
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
	public void executeGlobal(@NotNull JDA jda){
		Settings.close();
	}
	
	@Override
	public void executeGuild(@NotNull Guild guild){
	}
	
	@NotNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
}
