package fr.raksrinana.rsndiscord.api.irc.twitch;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class IRCUser{
	private final String user;
	private final String nick;
	private final String host;
	
	public IRCUser(@NotNull String infos){
		if(infos.contains("!")){
			nick = infos.substring(0, infos.indexOf("!"));
			user = infos.substring(infos.indexOf("!") + 1, infos.indexOf("@"));
			host = infos.substring(infos.indexOf("@") + 1);
		}
		else{
			user = infos;
			nick = infos;
			host = infos;
		}
	}
	
	@Override
	public String toString(){
		return getNick();
	}
}
