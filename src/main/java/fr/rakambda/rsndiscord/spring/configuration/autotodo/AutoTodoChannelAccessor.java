package fr.rakambda.rsndiscord.spring.configuration.autotodo;

import fr.rakambda.rsndiscord.spring.configuration.ChannelAccessor;
import fr.rakambda.rsndiscord.spring.storage.entity.ChannelType;
import fr.rakambda.rsndiscord.spring.storage.repository.ChannelRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutoTodoChannelAccessor extends ChannelAccessor{
	@Autowired
	public AutoTodoChannelAccessor(@NotNull ChannelRepository channelRepository){
		super(channelRepository, ChannelType.AUTO_TODO);
	}
	
	@Override
	@NotNull
	public String getName(){
		return "autoTodo.channels";
	}
}
