package fr.raksrinana.rsndiscord.api.irc.twitch;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
class TwitchBadge{
	private final String name;
	private final String version;
	
	public TwitchBadge(@NotNull String name, @NotNull String version){
		this.name = name;
		this.version = version;
	}
}
