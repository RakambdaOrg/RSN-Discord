package fr.raksrinana.rsndiscord.modules.irc.messages;

import fr.raksrinana.rsndiscord.modules.irc.twitch.IRCUser;
import lombok.Getter;

@Getter
public class ChannelJoinIRCMessage implements IIRCMessage{
	private final String channel;
	private final IRCUser user;
	
	public ChannelJoinIRCMessage(final IRCUser user, final String channel){
		this.user = user;
		this.channel = channel;
	}
}
