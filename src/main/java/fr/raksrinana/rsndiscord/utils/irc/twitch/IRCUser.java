package fr.raksrinana.rsndiscord.utils.irc.twitch;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class IRCUser{
	private final String user;
	private final String nick;
	private final String host;
	
	public IRCUser(@NonNull final String infos){
		if(infos.contains("!")){
			this.nick = infos.substring(0, infos.indexOf("!"));
			this.user = infos.substring(infos.indexOf("!") + 1, infos.indexOf("@"));
			this.host = infos.substring(infos.indexOf("@") + 1);
		}
		else{
			this.user = infos;
			this.nick = infos;
			this.host = infos;
		}
	}
	
	@Override
	public String toString(){
		return this.getNick();
	}
}
