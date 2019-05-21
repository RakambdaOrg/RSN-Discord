package fr.mrcraftcod.gunterdiscord.utils.player.trackfields;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 19/08/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-08-19
 */
interface AudioTrackUserFields<T>{
	String getName();
	
	default T parseObject(final Object value) throws IllegalArgumentException{
		//noinspection unchecked
		return (T) value;
	}
	
	default T valueForField(final T value){
		return value;
	}
}
