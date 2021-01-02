package fr.raksrinana.rsndiscord.modules.irc.twitch;

import fr.raksrinana.rsndiscord.modules.irc.messages.IIRCMessage;
import fr.raksrinana.rsndiscord.modules.irc.twitch.messages.ChannelMessageIRCMessage;
import lombok.Getter;
import lombok.NonNull;
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
	
	public TwitchChannelMessageIRCMessage(@NonNull ChannelMessageIRCMessage event){
		this.parent = event;
		this.badges = event.getTags().stream()
				.filter(tag -> Objects.equals("badges", tag.getKey()))
				.map(IRCTag::getValue)
				.flatMap(tag -> Arrays.stream(tag.split(","))
						.filter(v -> !v.isBlank())
						.map(value -> {
							var split = value.split("/");
							return new TwitchBadge(split[0], split[1]);
						})).collect(toList());
	}
	
	public Optional<TwitchBadge> getSub(){
		return getBadge("subscriber");
	}
	
	@NonNull
	private Optional<TwitchBadge> getBadge(String name){
		return getBadges().stream().filter(t -> Objects.equals(name, t.getName())).findFirst();
	}
	
	public Optional<TwitchBadge> getSubGifter(){
		return getBadge("sub-gifter");
	}
	
	public boolean isBroadcaster(){
		return getBadge("broadcaster").isPresent();
	}
	
	public boolean isModerator(){
		return parent.getTag("mod")
				.filter(tag -> Objects.equals(tag.getValue(), "1"))
				.isPresent();
	}
	
	public boolean isPartner(){
		return parent.getTag("partner").isPresent();
	}
}
