package fr.raksrinana.rsndiscord.modules.irc.twitch.messages;

import fr.raksrinana.rsndiscord.modules.irc.messages.IIRCMessage;
import fr.raksrinana.rsndiscord.modules.irc.twitch.IRCTag;
import lombok.Getter;
import java.util.List;

@Getter
public class ClearChatIRCMessage implements IIRCMessage{
	private final List<IRCTag> tags;
	private final String ircChannel;
	private final String user;
	
	public ClearChatIRCMessage(final List<IRCTag> tags, final String ircChannel, final String user){
		this.tags = tags;
		this.ircChannel = ircChannel;
		this.user = user;
	}
}
