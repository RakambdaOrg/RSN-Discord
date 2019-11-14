package fr.raksrinana.rsndiscord.utils.anilist.list;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NonNull;
import java.awt.Color;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum MediaListStatus{
	CURRENT(Color.GREEN, "Current"), PLANNING(Color.WHITE, "Planning"), COMPLETED(Color.BLACK, "Completed"), DROPPED(Color.RED, "Dropped"), PAUSED(Color.ORANGE, "Paused"), REPEATING(Color.YELLOW, "Repeating"), UNKNOWN(Color.MAGENTA, "Unknown");
	private final Color color;
	private final String display;
	
	MediaListStatus(@NonNull final Color color, @NonNull final String display){
		this.color = color;
		this.display = display;
	}
	
	@JsonCreator
	@NonNull
	public static MediaListStatus getFromString(@NonNull final String value){
		return MediaListStatus.valueOf(value);
	}
	
	@Override
	public String toString(){
		return this.display;
	}
}
