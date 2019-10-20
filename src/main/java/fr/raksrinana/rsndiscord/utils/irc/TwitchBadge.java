package fr.raksrinana.rsndiscord.utils.irc;

class TwitchBadge{
	private final String name;
	private final String version;
	
	public TwitchBadge(final String name, final String version){
		this.name = name;
		this.version = version;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getVersion(){
		return this.version;
	}
}
