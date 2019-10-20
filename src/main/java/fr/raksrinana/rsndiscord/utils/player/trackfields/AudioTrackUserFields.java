package fr.raksrinana.rsndiscord.utils.player.trackfields;

import javax.annotation.Nonnull;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 19/08/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-08-19
 */
interface AudioTrackUserFields<T>{
	@Nonnull
	default T parseObject(@Nonnull final Object value) throws IllegalArgumentException{
		//noinspection unchecked
		return (T) value;
	}
	
	@Nonnull
	default T valueForField(@Nonnull final T value){
		return value;
	}
	
	@Nonnull
	String getName();
}
