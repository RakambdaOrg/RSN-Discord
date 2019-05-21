package fr.mrcraftcod.gunterdiscord.utils.irc.events;

import fr.mrcraftcod.gunterdiscord.utils.irc.IRCUser;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
public class ChannelMessageIRCEvent extends AbstractIRCEvent{
	private final String message;
	
	public ChannelMessageIRCEvent(final IRCUser user, final String eventType, final String channel, final String message){
		super(user, eventType, channel);
		this.message = message;
	}
	
	public String getMessage(){
		return this.message;
	}
}
