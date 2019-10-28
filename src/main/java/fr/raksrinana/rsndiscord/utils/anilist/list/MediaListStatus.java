package fr.raksrinana.rsndiscord.utils.anilist.list;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.annotation.Nonnull;
import java.awt.Color;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-12.
 *
 * @author Thomas Couchoud
 * @since 2018-10-12
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum MediaListStatus{
	CURRENT(Color.GREEN, "Current"), PLANNING(Color.WHITE, "Planning"), COMPLETED(Color.BLACK, "Completed"), DROPPED(Color.RED, "Dropped"), PAUSED(Color.ORANGE, "Paused"), REPEATING(Color.YELLOW, "Repeating"), UNKNOWN(Color.MAGENTA, "Unknown");
	private final Color color;
	private final String display;
	
	MediaListStatus(@Nonnull final Color color, @Nonnull final String display){
		this.color = color;
		this.display = display;
	}
	
	@JsonCreator
	@Nonnull
	public static MediaListStatus getFromString(@Nonnull final String value){
		return MediaListStatus.valueOf(value);
	}
	
	@Override
	public String toString(){
		return this.display;
	}
	
	@Nonnull
	public Color getColor(){
		return this.color;
	}
}
