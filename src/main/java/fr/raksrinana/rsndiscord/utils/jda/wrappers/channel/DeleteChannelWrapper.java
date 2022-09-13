package fr.raksrinana.rsndiscord.utils.jda.wrappers.channel;

import fr.raksrinana.rsndiscord.utils.jda.ActionWrapper;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class DeleteChannelWrapper extends ActionWrapper<Void, AuditableRestAction<Void>>{
	private final TextChannel channel;
	
	public DeleteChannelWrapper(@NotNull TextChannel channel){
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
