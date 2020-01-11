package fr.raksrinana.rsndiscord.utils.irc.messages;

import fr.raksrinana.rsndiscord.utils.irc.twitch.IRCUser;
import lombok.Getter;

@Getter
public class ChannelLeftIRCMessage implements IRCMessage{
	private final String channel;
	private final IRCUser user;
	
	public ChannelLeftIRCMessage(final IRCUser user, final String channel){
		this.user = user;
		this.channel = channel;
	}
}
