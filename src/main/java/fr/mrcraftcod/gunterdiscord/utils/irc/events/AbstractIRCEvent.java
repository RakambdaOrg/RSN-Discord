package fr.mrcraftcod.gunterdiscord.utils.irc.events;

import fr.mrcraftcod.gunterdiscord.utils.irc.IRCUser;
import javax.annotation.Nonnull;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
@SuppressWarnings("WeakerAccess")
public class AbstractIRCEvent implements IRCEvent{
	private final IRCUser user;
	private final String eventType;
	private final String channel;
	
	public AbstractIRCEvent(@Nonnull final IRCUser user, @Nonnull final String eventType, @Nonnull final String channel){
		this.user = user;
		this.eventType = eventType;
		this.channel = channel;
	}
	
	@Nonnull
	public String getChannel(){
		return this.channel;
	}
	
	@Nonnull
	public String getEventType(){
		return this.eventType;
	}
	
	@Nonnull
	public IRCUser getUser(){
		return this.user;
	}
}
