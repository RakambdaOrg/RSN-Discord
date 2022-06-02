package fr.raksrinana.rsndiscord.runner.impl;

import fr.raksrinana.rsndiscord.runner.api.IScheduledRunner;
import fr.raksrinana.rsndiscord.runner.api.ScheduledRunner;
import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.schedule.ScheduleResult.COMPLETED;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
@Log4j2
public class ScheduleRunner implements IScheduledRunner{
	@Override
	public void executeGlobal(@NotNull JDA jda){
	}
	
	@Override
	public void executeGuild(@NotNull Guild guild) throws Exception{
		log.debug("Processing guild {}", guild);
		
		var guildConfiguration = Settings.get(guild);
		var valuesIterator = guildConfiguration.getScheduleHandlers().values().iterator();
		while(valuesIterator.hasNext()){
			var schedule = valuesIterator.next();
			schedule.process(guild).thenAccept(result -> {
				if(result == COMPLETED){
					valuesIterator.remove();
				}
			});
		}
	}
	
	@Override
	public long getDelay(){
		return 3;
	}
	
	@NotNull
	@Override
	public String getName(){
		return "Schedules action refresher";
	}
	
	@Override
	public long getPeriod(){
		return 1;
	}
	
	@NotNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
}
