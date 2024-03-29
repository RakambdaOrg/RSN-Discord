package fr.rakambda.rsndiscord.spring.configuration.scheduledevents;

import fr.rakambda.rsndiscord.spring.configuration.ChannelAccessor;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduledEventsChannelAccessor extends ChannelAccessor{
	@Autowired
	public ScheduledEventsChannelAccessor(@NotNull ChannelRepository channelRepository){
		super(channelRepository, ChannelType.SCHEDULED_EVENTS);
	}
	
	@Override
	@NotNull
	public String getName(){
		return "scheduled.events.channel";
	}
}
