package fr.mrcraftcod.gunterdiscord.utils.irc;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-02-25.
 *
 * @author Thomas Couchoud
 * @since 2019-02-25
 */
public class IRCUser{
	private final String user;
	private final String nick;
	private final String host;
	
	public IRCUser(String infos){
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
	
	public String getNick(){
		return nick;
	}
	
	public String getHost(){
		return host;
	}
	
	public String getUser(){
		return user;
	}
}
