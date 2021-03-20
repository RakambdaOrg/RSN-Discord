package fr.raksrinana.rsndiscord.api.irc.twitch.messages;

import fr.raksrinana.rsndiscord.api.irc.messages.IIRCMessage;
import fr.raksrinana.rsndiscord.api.irc.twitch.IRCTag;
import fr.raksrinana.rsndiscord.api.irc.twitch.IRCUser;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record ChannelMessageIRCMessage(@NotNull List<IRCTag> tags,
                                       @NotNull IRCUser user,
                                       @NotNull String channel,
                                       @NotNull String message) implements IIRCMessage{
	@NotNull
	public Optional<IRCTag> getTag(String name){
		return tags().stream().filter(t -> Objects.equals(name, t.key())).findFirst();
	}
	
	@NotNull
	public Optional<String> getCustomRewardId(){
		return tags().stream()
				.filter(tag -> Objects.equals(tag.key(), "custom-reward-id"))
				.map(IRCTag::value)
				.findFirst();
	}
}
