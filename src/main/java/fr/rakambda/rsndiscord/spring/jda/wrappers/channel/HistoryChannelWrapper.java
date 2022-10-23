package fr.rakambda.rsndiscord.spring.jda.wrappers.channel;

import fr.rakambda.rsndiscord.spring.jda.PagedActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.pagination.MessagePaginationAction;
import org.jetbrains.annotations.NotNull;
import java.util.List;

@Slf4j
public class HistoryChannelWrapper extends PagedActionWrapper<Message, MessagePaginationAction>{
	private final MessageChannel channel;
	
	public HistoryChannelWrapper(@NotNull MessageChannel channel){
		super(channel.getIterableHistory());
		this.channel = channel;
	}
	
	@Override
	protected void logSuccess(List<Message> value){
		log.info("Got {} history messages for channel {}", value.size(), channel);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to get history for channel {}", channel, throwable);
	}
}
