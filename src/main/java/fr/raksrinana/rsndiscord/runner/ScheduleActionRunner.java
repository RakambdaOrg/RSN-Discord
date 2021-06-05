package fr.raksrinana.rsndiscord.runner;

import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.scheduleaction.ScheduleActionResult.COMPLETED;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
@Log4j2
public class ScheduleActionRunner implements IScheduledRunner{
	@Getter
	private final JDA jda;
	
	public ScheduleActionRunner(JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		for(var guild : getJda().getGuilds()){
			log.debug("Processing guild {}", guild);
			
			var guildConfiguration = Settings.get(guild);
			for(var schedule : guildConfiguration.getScheduleHandler().values()){
				schedule.process(guild).thenAccept(result -> {
					if(result == COMPLETED){
						guildConfiguration.removeScheduleActionHandler(schedule.getSchedulerId());
					}
				});
			}
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
