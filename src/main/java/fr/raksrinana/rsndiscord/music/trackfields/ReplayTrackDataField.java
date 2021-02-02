package fr.raksrinana.rsndiscord.music.trackfields;

import org.jetbrains.annotations.NotNull;

public class ReplayTrackDataField implements AudioTrackDataFields<Boolean>{
	@NotNull
	public Boolean parseObject(@NotNull Object value) throws IllegalArgumentException{
		return (Boolean) value;
	}
	
	@Override
	@NotNull
	public String getName(){
		return "replay";
	}
}
