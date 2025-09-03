package fr.rakambda.rsndiscord.spring.configuration.scan;

import fr.rakambda.rsndiscord.spring.configuration.ChannelAccessor;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScanLinkReplacementChannelAccessor extends ChannelAccessor{
	@Autowired
	public ScanLinkReplacementChannelAccessor(@NonNull ChannelRepository channelRepository){
		super(channelRepository, ChannelType.SCAN_LINK_REPLACEMENT);
	}
	
	@Override
	@NonNull
	public String getName(){
		return "scan.link.replacement";
	}
}
