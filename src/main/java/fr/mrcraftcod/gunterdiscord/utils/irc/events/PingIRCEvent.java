package fr.mrcraftcod.gunterdiscord.utils.irc.events;

import fr.mrcraftcod.gunterdiscord.utils.irc.IRCUser;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
public class PingIRCEvent implements IRCEvent{
	private final IRCUser user;
	private final String eventType;
	
	public PingIRCEvent(IRCUser user, String eventType){
		this.user = user;
		this.eventType = eventType;
	}
	
	public String getEventType(){
		return eventType;
	}
	
	public IRCUser getUser(){
		return user;
	}
}
