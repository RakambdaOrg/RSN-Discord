package fr.mrcraftcod.gunterdiscord.utils.irc.messages;

import fr.mrcraftcod.gunterdiscord.utils.irc.IRCTag;
import java.util.List;

public class UserNoticeIRCMessage implements IRCMessage{
	private final List<IRCTag> tags;
	private final String message;
	
	public UserNoticeIRCMessage(final List<IRCTag> tags, final String message){
		this.tags = tags;
		this.message = message;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public List<IRCTag> getTags(){
		return this.tags;
	}
}
