package fr.raksrinana.rsndiscord.api.irc.twitch.messages;

import fr.raksrinana.rsndiscord.api.irc.messages.IIRCMessage;
import fr.raksrinana.rsndiscord.api.irc.twitch.IRCTag;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import java.util.List;

@Getter
public class ClearChatIRCMessage implements IIRCMessage{
	private final List<IRCTag> tags;
	private final String ircChannel;
	private final String user;
	
	public ClearChatIRCMessage(@NotNull List<IRCTag> tags, @NotNull String ircChannel, @NotNull String user){
		this.tags = tags;
		this.ircChannel = ircChannel;
		this.user = user;
	}
}
