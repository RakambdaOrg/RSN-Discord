package fr.raksrinana.rsndiscord.api.irc.twitch.messages;

import fr.raksrinana.rsndiscord.api.irc.messages.IIRCMessage;
import fr.raksrinana.rsndiscord.api.irc.twitch.IRCTag;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import java.util.List;

@Getter
public class UserStateIRCMessage implements IIRCMessage{
	private final List<IRCTag> tags;
	private final String channel;
	
	public UserStateIRCMessage(@NotNull List<IRCTag> tags, @NotNull String channel){
		this.tags = tags;
		this.channel = channel;
	}
}
