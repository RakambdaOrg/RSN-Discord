package fr.raksrinana.rsndiscord.utils.irc.messages;

import lombok.Getter;

@Getter
public class HostTargetIRCMessage implements IRCMessage{
	private final String ircChannel;
	private final String infos;
	
	public HostTargetIRCMessage(final String ircChannel, final String infos){
		this.ircChannel = ircChannel;
		this.infos = infos;
	}
}
