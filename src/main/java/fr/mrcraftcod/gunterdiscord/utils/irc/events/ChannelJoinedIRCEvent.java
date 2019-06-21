package fr.mrcraftcod.gunterdiscord.utils.irc.events;

import fr.mrcraftcod.gunterdiscord.utils.irc.IRCUser;
import javax.annotation.Nonnull;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
public class ChannelJoinedIRCEvent extends AbstractIRCEvent{
	public ChannelJoinedIRCEvent(@Nonnull final IRCUser user, @Nonnull final String eventType, @Nonnull final String channel){
		super(user, eventType, channel);
	}
}
