package fr.rakambda.rsndiscord.spring.configuration.rss;

import fr.rakambda.rsndiscord.spring.configuration.ChannelAccessor;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RssChannelAccessor extends ChannelAccessor{
	@Autowired
	public RssChannelAccessor(@NotNull ChannelRepository channelRepository){
		super(channelRepository, ChannelType.RSS);
	}
	
	@Override
	@NotNull
	public String getName(){
		return "rss.channels";
	}
}
