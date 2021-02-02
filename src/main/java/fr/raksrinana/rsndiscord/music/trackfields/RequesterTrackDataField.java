package fr.raksrinana.rsndiscord.music.trackfields;

import fr.raksrinana.rsndiscord.Main;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class RequesterTrackDataField implements AudioTrackDataFields<User>{
	public User parseObject(@NotNull Object value) throws IllegalArgumentException{
		return Main.getJda().retrieveUserById((long) value).complete();
	}
	
	@NotNull
	@Override
	public Object valueForField(@NotNull User value){
		return value.getIdLong();
	}
	
	@Override
	@NotNull
	public String getName(){
		return "requester";
	}
}
