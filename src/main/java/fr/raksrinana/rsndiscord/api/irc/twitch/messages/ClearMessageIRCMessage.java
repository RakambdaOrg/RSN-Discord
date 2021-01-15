package fr.raksrinana.rsndiscord.api.irc.twitch.messages;

import fr.raksrinana.rsndiscord.api.irc.messages.IIRCMessage;
import fr.raksrinana.rsndiscord.api.irc.twitch.IRCTag;
import lombok.Getter;
import java.util.List;

@Getter
public class ClearMessageIRCMessage implements IIRCMessage{
	private final List<IRCTag> tags;
	private final String ircChannel;
	private final String message;
	
	public ClearMessageIRCMessage(final List<IRCTag> tags, final String ircChannel, final String message){
		this.tags = tags;
		this.ircChannel = ircChannel;
		this.message = message;
	}
}
