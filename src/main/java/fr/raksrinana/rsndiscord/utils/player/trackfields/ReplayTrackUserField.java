package fr.raksrinana.rsndiscord.utils.player.trackfields;

import javax.annotation.Nonnull;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 19/08/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-08-19
 */
public class ReplayTrackUserField implements AudioTrackUserFields<Boolean>{
	@Nonnull
	public Boolean parseObject(@Nonnull final Object value) throws IllegalArgumentException{
		return (Boolean) value;
	}
	
	@Override
	@Nonnull
	public String getName(){
		return "replay";
	}
}
