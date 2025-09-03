package fr.rakambda.rsndiscord.spring.configuration.autotodo;

import fr.rakambda.rsndiscord.spring.configuration.ChannelAccessor;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutoTodoChannelAccessor extends ChannelAccessor{
	@Autowired
	public AutoTodoChannelAccessor(@NonNull ChannelRepository channelRepository){
		super(channelRepository, ChannelType.AUTO_TODO);
	}
	
	@Override
	@NonNull
	public String getName(){
		return "autoTodo.channels";
	}
}
