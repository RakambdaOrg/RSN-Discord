package fr.mrcraftcod.gunterdiscord.utils.player.trackfields;

import net.dv8tion.jda.api.entities.User;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 19/08/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-08-19
 */
public class RequesterTrackUserField implements AudioTrackUserFields<User>{
	@Override
	public String getName(){
		return "requester";
	}
}
