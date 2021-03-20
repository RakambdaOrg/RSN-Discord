package fr.raksrinana.rsndiscord.api.irc.twitch;

import org.jetbrains.annotations.NotNull;

public record IRCTag(@NotNull String key, @NotNull String value){
	@Override
	public String toString(){
		return key() + "=" + value();
	}
}
