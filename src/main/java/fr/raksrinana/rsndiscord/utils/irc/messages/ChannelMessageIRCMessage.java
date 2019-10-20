package fr.raksrinana.rsndiscord.utils.irc.messages;

import fr.raksrinana.rsndiscord.utils.irc.IRCTag;
import fr.raksrinana.rsndiscord.utils.irc.IRCUser;
import java.util.List;

public class ChannelMessageIRCMessage implements IRCMessage{
	private final List<IRCTag> tags;
	private final IRCUser user;
	private final String channel;
	private final String message;
	
	public ChannelMessageIRCMessage(final List<IRCTag> tags, final IRCUser user, final String channel, final String message){
		this.tags = tags;
		this.user = user;
		this.channel = channel;
		this.message = message;
	}
	
	public String getChannel(){
		return this.channel;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public List<IRCTag> getTags(){
		return this.tags;
	}
	
	public IRCUser getUser(){
		return this.user;
	}
}
