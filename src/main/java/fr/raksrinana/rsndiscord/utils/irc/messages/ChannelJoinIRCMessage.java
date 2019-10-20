package fr.raksrinana.rsndiscord.utils.irc.messages;

import fr.raksrinana.rsndiscord.utils.irc.IRCUser;

public class ChannelJoinIRCMessage implements IRCMessage{
	private final String channel;
	private final IRCUser user;
	
	public ChannelJoinIRCMessage(final IRCUser user, final String channel){
		this.user = user;
		this.channel = channel;
	}
	
	public String getChannel(){
		return this.channel;
	}
	
	public IRCUser getUser(){
		return this.user;
	}
}
