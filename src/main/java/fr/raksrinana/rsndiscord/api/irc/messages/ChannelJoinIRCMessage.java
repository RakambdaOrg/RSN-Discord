package fr.raksrinana.rsndiscord.api.irc.messages;

import fr.raksrinana.rsndiscord.api.irc.twitch.IRCUser;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class ChannelJoinIRCMessage implements IIRCMessage{
	private final String channel;
	private final IRCUser user;
	
	public ChannelJoinIRCMessage(@NotNull IRCUser user, @NotNull String channel){
		this.user = user;
		this.channel = channel;
	}
}
