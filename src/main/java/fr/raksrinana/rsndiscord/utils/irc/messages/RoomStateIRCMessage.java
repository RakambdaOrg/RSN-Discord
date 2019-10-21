package fr.raksrinana.rsndiscord.utils.irc.messages;

import fr.raksrinana.rsndiscord.utils.irc.IRCTag;
import java.util.List;

public class RoomStateIRCMessage implements IRCMessage{
	private final List<IRCTag> tags;
	private final String channel;
	
	public RoomStateIRCMessage(final List<IRCTag> tags, final String channel){
		this.tags = tags;
		this.channel = channel;
	}
	
	public String getChannel(){
		return this.channel;
	}
	
	public List<IRCTag> getTags(){
		return this.tags;
	}
}