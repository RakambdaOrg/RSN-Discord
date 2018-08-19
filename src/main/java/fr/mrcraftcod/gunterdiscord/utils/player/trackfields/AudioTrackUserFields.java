package fr.mrcraftcod.gunterdiscord.utils.player.trackfields;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 19/08/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-08-19
 */
abstract class AudioTrackUserFields<T>{
	public void fill(final TrackUserFields map, final T value){
		map.put(getName(), value);
	}
	
	protected abstract String getName();
	
	public T getOrDefault(final TrackUserFields map, final T defaultValue){
		return (T) map.getOrDefault(getName(), defaultValue);
	}
}
