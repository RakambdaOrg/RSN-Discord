package fr.raksrinana.rsndiscord.api.irc.twitch;

import lombok.Getter;

@Getter
public class IRCTag{
	private final String key;
	private final String value;
	
	public IRCTag(final String key, final String value){
		this.key = key;
		this.value = value;
	}
	
	@Override
	public String toString(){
		return this.getKey() + "=" + this.getValue();
	}
}
