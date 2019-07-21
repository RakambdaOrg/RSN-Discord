package fr.mrcraftcod.gunterdiscord.utils.irc.messages;

import fr.mrcraftcod.gunterdiscord.utils.irc.IRCTag;
import java.util.List;

public class UserNoticeIRCMessage implements IRCMessage{
	private final List<IRCTag> tags;
	private final String ircChannel;
	private final String message;
	
	public UserNoticeIRCMessage(List<IRCTag> tags, String ircChannel, String message){
		this.tags = tags;
		this.ircChannel = ircChannel;
		this.message = message;
	}
	
	public String getIrcChannel(){
		return ircChannel;
	}
	
	public String getMessage(){
		return message;
	}
	
	public List<IRCTag> getTags(){
		return tags;
	}
}
