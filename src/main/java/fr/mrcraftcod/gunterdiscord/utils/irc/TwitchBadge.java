package fr.mrcraftcod.gunterdiscord.utils.irc;

public class TwitchBadge{
	private final String name;
	private final String version;
	
	public TwitchBadge(String name, String version){
		this.name = name;
		this.version = version;
	}
	
	public String getName(){
		return name;
	}
	
	public String getVersion(){
		return version;
	}
}
