package fr.raksrinana.rsndiscord.api.irc.twitch;

import lombok.Getter;

@Getter
class TwitchBadge{
	private final String name;
	private final String version;
	
	public TwitchBadge(final String name, final String version){
		this.name = name;
		this.version = version;
	}
}
