package fr.raksrinana.rsndiscord.api.irc.messages;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class InfoMessageIRCMessage implements IIRCMessage{
	private final String message;
	
	public InfoMessageIRCMessage(@NotNull String message){
		this.message = message;
	}
}
