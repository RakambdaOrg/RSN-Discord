package fr.raksrinana.rsndiscord.api.irc.messages;

import lombok.Getter;

@Getter
public class InfoMessageIRCMessage implements IIRCMessage{
	private final String message;
	
	public InfoMessageIRCMessage(final String message){
		this.message = message;
	}
}
