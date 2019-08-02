package fr.mrcraftcod.gunterdiscord.utils.irc.messages;

import fr.mrcraftcod.gunterdiscord.utils.irc.IRCTag;
import java.util.List;

public class ClearChatIRCMessage implements IRCMessage{
	private final List<IRCTag> tags;
	private final String ircChannel;
	private final String user;
	
	public ClearChatIRCMessage(final List<IRCTag> tags, final String ircChannel, final String user){
		this.tags = tags;
		this.ircChannel = ircChannel;
		this.user = user;
	}
	
	public String getChannel(){
		return this.ircChannel;
	}
	
	public List<IRCTag> getTags(){
		return this.tags;
	}
	
	public String getUser(){
		return this.user;
	}
}
