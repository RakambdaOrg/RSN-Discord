package fr.raksrinana.rsndiscord.runner.impl;

import fr.raksrinana.rsndiscord.api.twitter.TwitterApi;
import fr.raksrinana.rsndiscord.runner.api.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.api.ScheduledRunner;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
public class ReconnectTwitterStreamRunner implements IScheduledRunner{
	@Override
	public void executeGlobal(@NotNull JDA jda){
		TwitterApi.registerStreamFilters(jda);
	}
	
	@Override
	public void executeGuild(@NotNull Guild guild){
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@NotNull
	@Override
	public String getName(){
		return "reconnectTwitterStream";
	}
	
	@Override
	public long getPeriod(){
		return 15;
	}
	
	@NotNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
}
