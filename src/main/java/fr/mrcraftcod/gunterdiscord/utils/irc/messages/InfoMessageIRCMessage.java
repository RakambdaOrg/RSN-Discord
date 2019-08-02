package fr.mrcraftcod.gunterdiscord.utils.irc.messages;

public class InfoMessageIRCMessage implements IRCMessage{
	private final String message;
	
	public InfoMessageIRCMessage(final String message){
		this.message = message;
	}
	
	public String getMessage(){
		return this.message;
	}
}
