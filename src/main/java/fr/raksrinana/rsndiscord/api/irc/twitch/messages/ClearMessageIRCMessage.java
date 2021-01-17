package fr.raksrinana.rsndiscord.api.irc.twitch.messages;

import fr.raksrinana.rsndiscord.api.irc.messages.IIRCMessage;
import fr.raksrinana.rsndiscord.api.irc.twitch.IRCTag;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import java.util.List;

@Getter
public class ClearMessageIRCMessage implements IIRCMessage{
	private final List<IRCTag> tags;
	private final String ircChannel;
	private final String message;
	
	public ClearMessageIRCMessage(@NotNull List<IRCTag> tags, @NotNull String ircChannel, @NotNull String message){
		this.tags = tags;
		this.ircChannel = ircChannel;
		this.message = message;
	}
}
