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
	
	public ChannelMessageIRCEvent(IRCUser user, String eventType, String channel, String message){
		super(user, eventType, channel);
		this.message = message;
	}
	
	public String getMessage(){
		return message;
	}
}
