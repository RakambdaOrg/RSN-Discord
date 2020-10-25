package fr.raksrinana.rsndiscord.modules.irc.twitch;

import fr.raksrinana.rsndiscord.modules.irc.messages.IIRCMessage;
import fr.raksrinana.rsndiscord.modules.irc.twitch.messages.ChannelMessageIRCMessage;
import lombok.Getter;
import lombok.NonNull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TwitchChannelMessageIRCMessage implements IIRCMessage{
	@Getter
	private final ChannelMessageIRCMessage parent;
	@Getter
	private final List<TwitchBadge> badges;
	
	public TwitchChannelMessageIRCMessage(@NonNull ChannelMessageIRCMessage event){
		this.parent = event;
		this.badges = event.getTags().stream().filter(t -> Objects.equals("badges", t.getKey())).map(IRCTag::getValue).flatMap(t -> Arrays.stream(t.split(",")).filter(v -> !v.isBlank()).map(v -> {
			final var split = v.split("/");
			return new TwitchBadge(split[0], split[1]);
		})).collect(Collectors.toList());
	}
	
	public Optional<TwitchBadge> getSub(){
		return getBadges().stream().filter(t -> Objects.equals("subscriber", t.getName())).findFirst();
	}
	
	public Optional<TwitchBadge> getSubGifter(){
		return getBadges().stream().filter(t -> Objects.equals("sub-gifter", t.getName())).findFirst();
	}
	
	public boolean isBroadcaster(){
		return getBadges().stream().anyMatch(t -> Objects.equals("broadcaster", t.getName()));
	}
	
	public boolean isModerator(){
		return getParent().getTags().stream().anyMatch(t -> Objects.equals("moderator", t.getKey()));
	}
	
	public boolean isPartner(){
		return getParent().getTags().stream().anyMatch(t -> Objects.equals("partner", t.getKey()));
	}
}
