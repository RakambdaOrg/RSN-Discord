package fr.raksrinana.rsndiscord.utils.player.trackfields;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 19/08/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-08-19
 */
interface AudioTrackUserFields<T>{
	@Nullable
	T parseObject(@Nonnull final Object value);
	
	@Nonnull
	default Object valueForField(@Nonnull final T value){
		return value;
	}
	
	@Nonnull
	String getName();
}
