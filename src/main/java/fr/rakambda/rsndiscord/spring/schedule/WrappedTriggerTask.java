package fr.rakambda.rsndiscord.spring.schedule;

import fr.rakambda.rsndiscord.spring.log.LogContext;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jspecify.annotations.NonNull;
import org.springframework.scheduling.TriggerContext;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import static net.dv8tion.jda.api.JDA.Status.CONNECTED;

@Slf4j
public abstract class WrappedTriggerTask implements TriggerTaskProvider{
	protected final JDA jda;
	
	private Instant nextRun = Instant.EPOCH;
	
	protected WrappedTriggerTask(@NonNull JDA jda){
		this.jda = jda;
	}
	
	@NonNull
	protected abstract String getName();
	
	protected abstract long getPeriod();
	
	@NonNull
	protected abstract TemporalUnit getPeriodUnit();
	
	protected abstract void executeGlobal(@NonNull JDA jda) throws Exception;
	
	protected abstract void executeGuild(@NonNull Guild guild) throws Exception;
	
	@Override
	public void run(){
		if(jda.getStatus() != CONNECTED){
			log.warn("JDA not connected, delaying task {}", getName());
			nextRun = Instant.now().plusSeconds(60);
			return;
		}
		
		log.info("Starting {} scheduler runner", getName());
		try{
			executeGlobal(jda);
			for(Guild guild : jda.getGuilds()){
				try(var ignored = LogContext.with(guild)){
					executeGuild(guild);
				}
			}
		}
		catch(Throwable e){
			log.error("Error while executing {} scheduled runner", getName(), e);
		}
		finally{
			nextRun = Instant.now().plus(getPeriod(), getPeriodUnit());
			log.info("{} scheduled runner done", getName());
		}
	}
	
	@Override
	public Instant nextExecution(@NonNull TriggerContext triggerContext){
		return nextRun;
	}
}
