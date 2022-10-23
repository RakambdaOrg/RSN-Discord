package fr.rakambda.rsndiscord.spring.configuration.twitter;

import fr.rakambda.rsndiscord.spring.configuration.ChannelAccessor;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TwitterChannelAccessor extends ChannelAccessor{
	@Autowired
	public TwitterChannelAccessor(@NotNull ChannelRepository channelRepository){
		super(channelRepository, ChannelType.TWITTER);
	}
	
	@Override
	@NotNull
	public String getName(){
		return "twitter.channels";
	}
}
