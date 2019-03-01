package fr.mrcraftcod.gunterdiscord.utils.irc.events;

import fr.mrcraftcod.gunterdiscord.utils.irc.IRCUser;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
public class AbstractIRCEvent implements IRCEvent{
	private final IRCUser user;
	private final String eventType;
	private final String channel;
	
	public AbstractIRCEvent(IRCUser user, String eventType, String channel){
		this.user = user;
		this.eventType = eventType;
		this.channel = channel;
	}
	
	public String getChannel(){
		return channel;
	}
	
	public String getEventType(){
		return eventType;
	}
	
	public IRCUser getUser(){
		return user;
	}
}
