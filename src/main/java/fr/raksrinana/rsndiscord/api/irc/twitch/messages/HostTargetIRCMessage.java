package fr.raksrinana.rsndiscord.api.irc.twitch.messages;

import fr.raksrinana.rsndiscord.api.irc.messages.IIRCMessage;
import lombok.Getter;

@Getter
public class HostTargetIRCMessage implements IIRCMessage{
	private final String ircChannel;
	private final String infos;
	
	public HostTargetIRCMessage(final String ircChannel, final String infos){
		this.ircChannel = ircChannel;
		this.infos = infos;
	}
}