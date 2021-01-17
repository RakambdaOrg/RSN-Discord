package fr.raksrinana.rsndiscord.api.irc.twitch;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class IRCTag{
	private final String key;
	private final String value;
	
	@NotNull
	public IRCTag(@NotNull String key, @NotNull String value){
		this.key = key;
		this.value = value;
	}
	
	@Override
	public String toString(){
		return getKey() + "=" + getValue();
	}
}
