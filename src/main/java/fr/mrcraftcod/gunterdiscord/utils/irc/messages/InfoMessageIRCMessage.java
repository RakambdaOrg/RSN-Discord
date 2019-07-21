package fr.mrcraftcod.gunterdiscord.utils.irc.messages;

public class InfoMessageIRCMessage implements IRCMessage{
	private final String message;
	
	public InfoMessageIRCMessage(String message){
		this.message = message;
	}
	
	public String getMessage(){
		return message;
	}
}
