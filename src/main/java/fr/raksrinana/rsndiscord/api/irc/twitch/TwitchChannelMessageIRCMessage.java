package fr.raksrinana.rsndiscord.api.irc.twitch;

import fr.raksrinana.rsndiscord.api.irc.messages.IIRCMessage;
import fr.raksrinana.rsndiscord.api.irc.twitch.messages.ChannelMessageIRCMessage;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static java.util.stream.Collectors.toList;

public class TwitchChannelMessageIRCMessage implements IIRCMessage{
	@Getter
	private final ChannelMessageIRCMessage parent;
	@Getter
	private final List<TwitchBadge> badges;
	
	public TwitchChannelMessageIRCMessage(@NotNull ChannelMessageIRCMessage event){
		parent = event;
		badges = event.tags().stream()
				.filter(tag -> Objects.equals("badges", tag.key()))
				.map(IRCTag::value)
				.flatMap(tag -> Arrays.stream(tag.split(","))
						.filter(v -> !v.isBlank())
						.map(value -> {
							var split = value.split("/");
							return new TwitchBadge(split[0], split[1]);
						})).collect(toList());
	}
	
	@NotNull
	public Optional<TwitchBadge> getSub(){
		return getBadge("subscriber");
	}
	
	@NotNull
	private Optional<TwitchBadge> getBadge(String name){
		return getBadges().stream().filter(t -> Objects.equals(name, t.getName())).findFirst();
	}
	
	@NotNull
	public Optional<TwitchBadge> getSubGifter(){
		return getBadge("sub-gifter");
	}
	
	public boolean isBroadcaster(){
		return getBadge("broadcaster").isPresent();
	}
	
	public boolean isModerator(){
		return parent.getTag("mod")
				.filter(tag -> Objects.equals(tag.value(), "1"))
				.isPresent();
	}
	
	public boolean isPartner(){
		return parent.getTag("partner").isPresent();
	}
}
