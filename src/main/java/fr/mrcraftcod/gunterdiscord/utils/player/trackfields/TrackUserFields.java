package fr.mrcraftcod.gunterdiscord.utils.player.trackfields;

import java.util.HashMap;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 19/08/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-08-19
 */
public class TrackUserFields{
	private final HashMap<String, Object> map;
	
	public TrackUserFields(){
		this.map = new HashMap<>();
	}
	
	public <T> void fill(final AudioTrackUserFields<T> field, final T value){
		map.put(field.getName(), value);
	}
	
	public <T> T getOrDefault(final AudioTrackUserFields<T> field, final T defaultValue){
		return get(field).orElse(defaultValue);
	}
	
	public <T> Optional<T> get(final AudioTrackUserFields<T> field){
		return Optional.ofNullable(map.get(field.getName())).map(value -> {
			try{
				return (T) value;
			}
			catch(final ClassCastException ignored){
			}
			return null;
		});
	}
}
