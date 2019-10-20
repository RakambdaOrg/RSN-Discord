package fr.raksrinana.rsndiscord.utils.irc.messages;

public class HostTargetIRCMessage implements IRCMessage{
	private final String ircChannel;
	private final String infos;
	
	public HostTargetIRCMessage(final String ircChannel, final String infos){
		this.ircChannel = ircChannel;
		this.infos = infos;
	}
	
	public String getChannel(){
		return this.ircChannel;
	}
	
	public String getInfos(){
		return this.infos;
	}
}
