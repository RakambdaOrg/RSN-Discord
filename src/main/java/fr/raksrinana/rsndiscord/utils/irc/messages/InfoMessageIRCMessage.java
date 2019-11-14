package fr.raksrinana.rsndiscord.utils.irc.messages;

import lombok.Getter;

@Getter
public class InfoMessageIRCMessage implements IRCMessage{
	private final String message;
	
	public InfoMessageIRCMessage(final String message){
		this.message = message;
	}
}
