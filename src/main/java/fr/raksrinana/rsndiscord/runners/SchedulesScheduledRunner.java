package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleUtils;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import java.time.LocalDateTime;
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
		final var currentDate = LocalDateTime.now();
		for(final var guild : this.getJda().getGuilds()){
			Log.getLogger(guild).debug("Processing guild {}", guild);
			final var it = Settings.get(guild).getSchedules().iterator();
			while(it.hasNext()){
				final var schedule = it.next();
				if(currentDate.isAfter(schedule.getScheduleDate())){
					for(final var handler : ScheduleUtils.getHandlers()){
						if(handler.acceptTag(schedule.getTag())){
							if(handler.accept(schedule)){
								it.remove();
								break;
							}
						}
					}
				}
				else{
					final var embed = ScheduleUtils.getEmbedFor(schedule);
					Optional.ofNullable(schedule.getReminderCountdownMessage()).flatMap(MessageConfiguration::getMessage).ifPresentOrElse(message -> Actions.editMessage(message, embed), () -> schedule.getChannel().getChannel().ifPresent(channel -> Actions.sendMessage(channel, "", embed).thenAccept(message -> schedule.setReminderCountdownMessage(new MessageConfiguration(message)))));
				}
			}
		}
	}
	
	@NonNull
	@Override
	public String getName(){
		return "schedules refresher";
	}
	
	@Override
	public long getDelay(){
		return 3;
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
