package fr.raksrinana.rsndiscord.utils.irc.twitch.messages;

import fr.raksrinana.rsndiscord.utils.irc.messages.IRCMessage;
import fr.raksrinana.rsndiscord.utils.irc.twitch.IRCTag;
import fr.raksrinana.rsndiscord.utils.irc.twitch.IRCUser;
import lombok.Getter;
import java.util.List;

@Getter
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
}