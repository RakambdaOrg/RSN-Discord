package fr.rakambda.rsndiscord.spring.configuration.anilist;

import fr.rakambda.rsndiscord.spring.configuration.ChannelAccessor;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AnilistMediaChangeChannelAccessor extends ChannelAccessor{
	@Autowired
	public AnilistMediaChangeChannelAccessor(@NonNull ChannelRepository channelRepository){
		super(channelRepository, ChannelType.ANILIST_MEDIA_CHANGE);
	}
	
	@Override
	@NonNull
	public String getName(){
		return "anilist.channels.mediaChange";
	}
}
