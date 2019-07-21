package fr.mrcraftcod.gunterdiscord.utils.irc.messages;

import fr.mrcraftcod.gunterdiscord.utils.irc.IRCTag;
import java.util.List;

public class RoomStateIRCMessage implements IRCMessage{
	private final List<IRCTag> tags;
	private final String channel;
	
	public RoomStateIRCMessage(List<IRCTag> tags, String channel){
		this.tags = tags;
		this.channel = channel;
	}
	
	public String getChannel(){
		return channel;
	}
	
	public List<IRCTag> getTags(){
		return tags;
	}
}
