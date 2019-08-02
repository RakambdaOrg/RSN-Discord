package fr.mrcraftcod.gunterdiscord.utils.irc.messages;

import fr.mrcraftcod.gunterdiscord.utils.irc.IRCTag;
import java.util.List;

public class UserStateIRCMessage implements IRCMessage{
	private final List<IRCTag> tags;
	private final String channel;
	
	public UserStateIRCMessage(final List<IRCTag> tags, final String channel){
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
