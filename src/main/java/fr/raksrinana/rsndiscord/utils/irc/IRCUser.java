package fr.raksrinana.rsndiscord.utils.irc;

import javax.annotation.Nonnull;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
@SuppressWarnings("WeakerAccess")
public class IRCUser{
	private final String user;
	private final String nick;
	private final String host;
	
	public IRCUser(@Nonnull final String infos){
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
	
	@Nonnull
	public String getNick(){
		return this.nick;
	}
	
	@Nonnull
	public String getHost(){
		return this.host;
	}
	
	@Nonnull
	public String getUser(){
		return this.user;
	}
}
