package fr.raksrinana.rsndiscord.utils.music.trackfields;

import lombok.NonNull;

interface AudioTrackDataFields<T>{
	T parseObject(@NonNull final Object value);
	
	@NonNull
	default Object valueForField(@NonNull final T value){
		return value;
	}
	
	@NonNull String getName();
}
