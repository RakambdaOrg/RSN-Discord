package fr.mrcraftcod.gunterdiscord.utils.irc.events;

import fr.mrcraftcod.gunterdiscord.utils.irc.IRCUser;
import javax.annotation.Nonnull;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
public class ChannelMessageIRCEvent extends AbstractIRCEvent{
	private final String message;
	
	public ChannelMessageIRCEvent(@Nonnull final IRCUser user, @Nonnull final String eventType, @Nonnull final String channel, @Nonnull final String message){
		super(user, eventType, channel);
		this.message = message;
	}
	
	@Nonnull
	public String getMessage(){
		return this.message;
	}
}
