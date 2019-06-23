package fr.mrcraftcod.gunterdiscord.utils.player.trackfields;

import javax.annotation.Nonnull;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 19/08/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-08-19
 */
public class ReplayTrackUserField implements AudioTrackUserFields<Boolean>{
	@Override
	@Nonnull
	public String getName(){
		return "replay";
	}
}
