package fr.raksrinana.rsndiscord.music.trackfields;

import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Optional;
import static java.util.Optional.ofNullable;

public class TrackUserFields{
	private final HashMap<String, Object> map;
	
	public TrackUserFields(){
		map = new HashMap<>();
	}
	
	public <T> void fill(@NotNull AudioTrackDataFields<T> field, @NotNull T value){
		map.put(field.getName(), field.valueForField(value));
	}
	
	@NotNull
	public <T> Optional<T> get(@NotNull AudioTrackDataFields<T> field){
		return ofNullable(map.get(field.getName())).map(field::parseObject);
	}
}
