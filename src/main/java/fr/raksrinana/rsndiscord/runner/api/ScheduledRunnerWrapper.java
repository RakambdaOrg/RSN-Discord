package fr.raksrinana.rsndiscord.runner.api;

import fr.raksrinana.rsndiscord.log.LogContext;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

@Getter
@Log4j2
public class ScheduledRunnerWrapper implements Runnable{
	private final IScheduledRunner runner;
	private final JDA jda;
	
	public ScheduledRunnerWrapper(@NotNull IScheduledRunner runner, @NotNull JDA jda){
		this.runner = runner;
		this.jda = jda;
		log.info("Created {} scheduler runner", runner.getName());
	}
	
	@Override
	public void run(){
		log.info("Starting {} scheduler runner", getRunner().getName());
		try{
			getRunner().executeGlobal(getJda());
			for(Guild guild : getJda().getGuilds()){
				try(var context = LogContext.with(guild)){
					getRunner().executeGuild(guild);
				}
			}
		}
		catch(Exception e){
			log.error("Error while executing {} scheduled runner", getRunner().getName(), e);
		}
		log.info("{} scheduled runner done", getRunner().getName());
	}
}
