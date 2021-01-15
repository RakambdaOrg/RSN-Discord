package fr.raksrinana.rsndiscord.music.trackfields;

import lombok.NonNull;

public class ReplayTrackDataField implements AudioTrackDataFields<Boolean>{
	@NonNull
	public Boolean parseObject(@NonNull final Object value) throws IllegalArgumentException{
		return (Boolean) value;
	}
	
	@Override
	@NonNull
	public String getName(){
		return "replay";
	}
}
