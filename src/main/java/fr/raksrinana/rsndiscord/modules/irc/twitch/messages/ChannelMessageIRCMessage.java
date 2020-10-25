package fr.raksrinana.rsndiscord.modules.irc.twitch.messages;

import fr.raksrinana.rsndiscord.modules.irc.messages.IIRCMessage;
import fr.raksrinana.rsndiscord.modules.irc.twitch.IRCTag;
import fr.raksrinana.rsndiscord.modules.irc.twitch.IRCUser;
import lombok.Getter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
public class ChannelMessageIRCMessage implements IIRCMessage{
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
	
	public Optional<String> getCustomRewardId(){
		return getTags().stream()
				.filter(tag -> Objects.equals(tag.getKey(), "custom-reward-id"))
				.map(IRCTag::getValue)
				.findFirst();
	}
}
