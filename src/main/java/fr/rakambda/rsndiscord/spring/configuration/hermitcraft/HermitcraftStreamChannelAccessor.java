package fr.rakambda.rsndiscord.spring.configuration.hermitcraft;

import fr.rakambda.rsndiscord.spring.configuration.ChannelAccessor;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HermitcraftStreamChannelAccessor extends ChannelAccessor{
	@Autowired
	public HermitcraftStreamChannelAccessor(ChannelRepository channelRepository){
		super(channelRepository, ChannelType.HERMITCRAFT_LIVE);
	}
	
	@Override
	@NotNull
	public String getName(){
		return "hermitcraft.channels.stream";
	}
}
