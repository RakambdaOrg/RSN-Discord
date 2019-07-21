package fr.mrcraftcod.gunterdiscord.utils.irc;

public class IRCTag{
	private final String key;
	private final String value;
	
	public IRCTag(String key, String value){
		this.key = key;
		this.value = value;
	}
	
	@Override
	public String toString(){
		return this.getKey() + "=" + this.getValue();
	}
	
	public String getKey(){
		return key;
	}
	
	public String getTrigram(){
		return getValue().substring(0, 3).toUpperCase();
	}
	
	public String getValue(){
		return value;
	}
}
