package fr.raksrinana.rsndiscord.utils.irc.twitch;

import java.util.Objects;

public enum TwitchMessageId{
	NONE(""), UNKNOWN("unknown"), HIGHLIGHTED_MESSAGE("highlighted-message");
	private final String name;
	
	TwitchMessageId(String name){
		this.name = name;
	}
	
	public static TwitchMessageId getFromName(String name){
		for(final var msgId : TwitchMessageId.values()){
			if(Objects.equals(name, msgId.name)){
				return msgId;
			}
		}
		return UNKNOWN;
	}
}
