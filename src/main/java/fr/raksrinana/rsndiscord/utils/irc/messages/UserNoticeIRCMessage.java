package fr.raksrinana.rsndiscord.utils.irc.messages;

import fr.raksrinana.rsndiscord.utils.irc.IRCTag;
import lombok.Getter;
import java.util.List;

@Getter
public class UserNoticeIRCMessage implements IRCMessage{
	private final List<IRCTag> tags;
	private final String ircChannel;
	private final String message;
	
	public UserNoticeIRCMessage(final List<IRCTag> tags, final String ircChannel, final String message){
		this.tags = tags;
		this.ircChannel = ircChannel;
		this.message = message;
	}
}
