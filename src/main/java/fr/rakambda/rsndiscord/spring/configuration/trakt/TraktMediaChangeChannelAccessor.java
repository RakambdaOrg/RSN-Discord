package fr.rakambda.rsndiscord.spring.configuration.trakt;

import fr.rakambda.rsndiscord.spring.configuration.ChannelAccessor;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TraktMediaChangeChannelAccessor extends ChannelAccessor{
	@Autowired
	public TraktMediaChangeChannelAccessor(@NonNull ChannelRepository channelRepository){
		super(channelRepository, ChannelType.TRAKT_MEDIA_CHANGE);
	}
	
	@Override
	@NonNull
	public String getName(){
		return "trakt.channels.mediaChange";
	}
}
