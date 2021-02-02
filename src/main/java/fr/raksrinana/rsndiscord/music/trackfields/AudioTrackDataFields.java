package fr.raksrinana.rsndiscord.music.trackfields;

import org.jetbrains.annotations.NotNull;

interface AudioTrackDataFields<T>{
	T parseObject(@NotNull Object value);
	
	@NotNull
	default Object valueForField(@NotNull T value){
		return value;
	}
	
	@NotNull String getName();
}
