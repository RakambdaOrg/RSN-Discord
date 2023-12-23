package fr.rakambda.rsndiscord.spring.configuration.simkl;

import fr.rakambda.rsndiscord.spring.configuration.ChannelAccessor;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SimklMediaChangeChannelAccessor extends ChannelAccessor{
	@Autowired
	public SimklMediaChangeChannelAccessor(@NotNull ChannelRepository channelRepository){
		super(channelRepository, ChannelType.SIMKL_MEDIA_CHANGE);
	}
	
	@Override
	@NotNull
	public String getName(){
		return "simkl.channels.mediaChange";
	}
}
