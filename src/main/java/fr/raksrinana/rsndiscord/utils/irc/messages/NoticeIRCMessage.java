package fr.raksrinana.rsndiscord.utils.irc.messages;

import fr.raksrinana.rsndiscord.utils.irc.IRCTag;
import java.util.List;

public class NoticeIRCMessage implements IRCMessage{
	private final List<IRCTag> tags;
	private final String ircChannel;
	private final String message;
	
	public NoticeIRCMessage(final List<IRCTag> tags, final String ircChannel, final String message){
		this.tags = tags;
		this.ircChannel = ircChannel;
		this.message = message;
	}
	
	public String getChannel(){
		return this.ircChannel;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public List<IRCTag> getTags(){
		return this.tags;
	}
}
