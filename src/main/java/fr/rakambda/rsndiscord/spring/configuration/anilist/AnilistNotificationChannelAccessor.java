package fr.rakambda.rsndiscord.spring.configuration.anilist;

import fr.rakambda.rsndiscord.spring.configuration.ChannelAccessor;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AnilistNotificationChannelAccessor extends ChannelAccessor{
	@Autowired
	public AnilistNotificationChannelAccessor(@NonNull ChannelRepository channelRepository){
		super(channelRepository, ChannelType.ANILIST_NOTIFICATION);
	}
	
	@Override
	@NonNull
	public String getName(){
		return "anilist.channels.notification";
	}
}
