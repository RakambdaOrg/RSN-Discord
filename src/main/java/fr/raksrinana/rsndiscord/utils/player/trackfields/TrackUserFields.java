package fr.raksrinana.rsndiscord.utils.player.trackfields;

import javax.annotation.Nonnull;
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
	
	public <T> void fill(@Nonnull final AudioTrackUserFields<T> field, @Nonnull final T value){
		this.map.put(field.getName(), field.valueForField(value));
	}
	
	@Nonnull
	public <T> Optional<T> get(@Nonnull final AudioTrackUserFields<T> field){
		return Optional.ofNullable(this.map.get(field.getName())).map(field::parseObject);
	}
}
