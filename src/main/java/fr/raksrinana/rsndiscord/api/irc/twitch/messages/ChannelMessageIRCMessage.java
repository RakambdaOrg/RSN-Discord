package fr.raksrinana.rsndiscord.api.irc.twitch.messages;

import fr.raksrinana.rsndiscord.api.irc.messages.IIRCMessage;
import fr.raksrinana.rsndiscord.api.irc.twitch.IRCTag;
import fr.raksrinana.rsndiscord.api.irc.twitch.IRCUser;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
public class ChannelMessageIRCMessage implements IIRCMessage{
	private final List<IRCTag> tags;
	private final IRCUser user;
	private final String channel;
	private final String message;
	
	public ChannelMessageIRCMessage(@NotNull List<IRCTag> tags, @NotNull IRCUser user, @NotNull String channel, @NotNull String message){
		this.tags = tags;
		this.user = user;
		this.channel = channel;
		this.message = message;
	}
	
	@NotNull
	public Optional<IRCTag> getTag(String name){
		return getTags().stream().filter(t -> Objects.equals(name, t.getKey())).findFirst();
	}
	
	@NotNull
	public Optional<String> getCustomRewardId(){
		return getTags().stream()
				.filter(tag -> Objects.equals(tag.getKey(), "custom-reward-id"))
				.map(IRCTag::getValue)
				.findFirst();
	}
}
