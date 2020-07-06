package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleUtils;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class SchedulesScheduledRunner implements ScheduledRunner{
	@Getter
	private final JDA jda;
	
	public SchedulesScheduledRunner(JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		final var currentDate = ZonedDateTime.now();
		for(final var guild : this.getJda().getGuilds()){
			Log.getLogger(guild).debug("Processing guild {}", guild);
			for(ScheduleConfiguration schedule : Settings.get(guild).getSchedules()){
				if(currentDate.isAfter(schedule.getScheduleDate())){
					for(final var handler : ScheduleUtils.getHandlers()){
						if(handler.acceptTag(schedule.getTag())){
							if(handler.accept(schedule)){
								Settings.get(guild).removeSchedule(schedule);
								break;
							}
						}
					}
				}
				else{
					Optional.ofNullable(schedule.getReminderCountdownMessage()).flatMap(MessageConfiguration::getMessage).ifPresent(message -> Actions.editMessage(message, ScheduleUtils.getEmbedFor(message.getGuild(), schedule)));
				}
			}
		}
	}
	
	@Override
	public long getDelay(){
		return 3;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "schedules refresher";
	}
	
	@Override
	public long getPeriod(){
		return 14;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
}
