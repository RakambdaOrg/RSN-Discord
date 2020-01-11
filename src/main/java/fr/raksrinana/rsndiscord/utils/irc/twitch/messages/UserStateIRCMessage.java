package fr.raksrinana.rsndiscord.utils.irc.twitch.messages;

import fr.raksrinana.rsndiscord.utils.irc.messages.IRCMessage;
import fr.raksrinana.rsndiscord.utils.irc.twitch.IRCTag;
import lombok.Getter;
import java.util.List;

@Getter
public class UserStateIRCMessage implements IRCMessage{
	private final List<IRCTag> tags;
	private final String channel;
	
	public UserStateIRCMessage(final List<IRCTag> tags, final String channel){
		this.tags = tags;
		this.channel = channel;
	}
}
