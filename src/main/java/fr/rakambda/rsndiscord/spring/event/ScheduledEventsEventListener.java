package fr.rakambda.rsndiscord.spring.event;

import fr.rakambda.rsndiscord.spring.scheduledevents.ScheduledEventsService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateDescriptionEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateEndTimeEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateLocationEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStartTimeEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledEventsEventListener extends ListenerAdapter{
	private final ScheduledEventsService scheduledEventsService;

	@Autowired
    public ScheduledEventsEventListener(ScheduledEventsService scheduledEventsService) {
        this.scheduledEventsService = scheduledEventsService;
    }
	
	@Override
	public void onScheduledEventCreate(ScheduledEventCreateEvent event){
		scheduledEventsService.update(event.getGuild());
	}
	
    @Override
	public void onScheduledEventUpdateDescription(ScheduledEventUpdateDescriptionEvent event){
	    scheduledEventsService.update(event.getGuild());
	}
	
	@Override
	public void onScheduledEventUpdateEndTime(ScheduledEventUpdateEndTimeEvent event){
		scheduledEventsService.update(event.getGuild());
	}
	
	@Override
	public void onScheduledEventUpdateLocation(ScheduledEventUpdateLocationEvent event){
		scheduledEventsService.update(event.getGuild());
	}
	
	@Override
	public void onScheduledEventUpdateName(ScheduledEventUpdateNameEvent event){
		scheduledEventsService.update(event.getGuild());
	}
	
	@Override
	public void onScheduledEventUpdateStartTime(ScheduledEventUpdateStartTimeEvent event){
		scheduledEventsService.update(event.getGuild());
	}
	
	@Override
	public void onScheduledEventUpdateStatus(ScheduledEventUpdateStatusEvent event){
		scheduledEventsService.update(event.getGuild());
	}
	
	@Override
	public void onScheduledEventDelete(ScheduledEventDeleteEvent event){
		scheduledEventsService.update(event.getGuild());
	}
}
