package fr.raksrinana.rsndiscord.modules.irc.twitch.messages;

import fr.raksrinana.rsndiscord.modules.irc.messages.IIRCMessage;
import fr.raksrinana.rsndiscord.modules.irc.twitch.IRCTag;
import lombok.Getter;
import java.util.List;

@Getter
public class UserStateIRCMessage implements IIRCMessage{
	private final List<IRCTag> tags;
	private final String channel;
	
	public UserStateIRCMessage(final List<IRCTag> tags, final String channel){
		this.tags = tags;
		this.channel = channel;
	}
}
