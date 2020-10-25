package fr.raksrinana.rsndiscord.modules.irc.twitch.messages;

import fr.raksrinana.rsndiscord.modules.irc.messages.IIRCMessage;
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
