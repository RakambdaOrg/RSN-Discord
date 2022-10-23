package fr.rakambda.rsndiscord.spring.configuration.anilist;

import fr.rakambda.rsndiscord.spring.configuration.ChannelAccessor;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AnilistMediaChangeChannelAccessor extends ChannelAccessor{
	@Autowired
	public AnilistMediaChangeChannelAccessor(@NotNull ChannelRepository channelRepository){
		super(channelRepository, ChannelType.ANILIST_MEDIA_CHANGE);
	}
	
	@Override
	@NotNull
	public String getName(){
		return "anilist.channels.mediaChange";
	}
}
