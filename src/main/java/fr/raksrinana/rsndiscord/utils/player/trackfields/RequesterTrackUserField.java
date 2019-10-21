package fr.raksrinana.rsndiscord.utils.player.trackfields;

import fr.raksrinana.rsndiscord.Main;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 19/08/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-08-19
 */
public class RequesterTrackUserField implements AudioTrackUserFields<User>{
	@Nullable
	public User parseObject(@Nonnull final Object value) throws IllegalArgumentException{
		return Main.getJDA().getUserById((long) value);
	}
	
	@Nonnull
	@Override
	public Object valueForField(@Nonnull User value){
		return value.getIdLong();
	}
	
	@Override
	@Nonnull
	public String getName(){
		return "requester";
	}
}
