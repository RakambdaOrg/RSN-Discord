package fr.raksrinana.rsndiscord.runner;

import fr.raksrinana.rsndiscord.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
@Log4j2
public class SchedulesRunner implements IScheduledRunner{
	@Getter
	private final JDA jda;
	
	public SchedulesRunner(JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		var currentDate = ZonedDateTime.now();
		for(var guild : getJda().getGuilds()){
			log.debug("Processing guild {}", guild);
			for(var schedule : Settings.get(guild).getSchedules()){
				if(currentDate.isAfter(schedule.getScheduleDate())){
					for(var handler : ScheduleUtils.getHandlers()){
						if(handler.acceptTag(schedule.getTag())){
							if(handler.accept(guild, schedule)){
								Settings.get(guild).removeSchedule(schedule);
								break;
							}
						}
					}
				}
				else{
					ofNullable(schedule.getReminderCountdownMessage())
							.flatMap(MessageConfiguration::getMessage)
							.ifPresent(message -> JDAWrappers.edit(message, ScheduleUtils.getEmbedFor(message.getGuild(), schedule)).submit());
				}
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
		return "Schedules refresher";
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
