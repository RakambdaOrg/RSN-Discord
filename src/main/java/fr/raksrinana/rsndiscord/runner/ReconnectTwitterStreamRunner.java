package fr.raksrinana.rsndiscord.runner;

import fr.raksrinana.rsndiscord.api.twitter.TwitterApi;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
public class ReconnectTwitterStreamRunner implements IScheduledRunner{
	private final JDA jda;
	
	public ReconnectTwitterStreamRunner(@NotNull JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		TwitterApi.registerStreamFilters(jda);
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
