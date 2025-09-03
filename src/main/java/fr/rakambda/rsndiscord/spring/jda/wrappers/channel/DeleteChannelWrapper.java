package fr.rakambda.rsndiscord.spring.jda.wrappers.channel;

import fr.rakambda.rsndiscord.spring.jda.ActionWrapper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jspecify.annotations.NonNull;

@Slf4j
public class DeleteChannelWrapper extends ActionWrapper<Void, AuditableRestAction<Void>>{
	private final TextChannel channel;
	
	public DeleteChannelWrapper(@NonNull TextChannel channel){
		super(channel.delete());
		this.channel = channel;
	}
	
	@Override
	protected void logSuccess(Void value){
		log.info("Deleted channel {}", channel);
	}
	
	@Override
	protected void logException(Throwable throwable){
		log.error("Failed to delete channel {}", channel, throwable);
	}
}
