package fr.raksrinana.rsndiscord.api.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum MediaSeason{
	FALL("Fall"),
	SPRING("Spring"),
	SUMMER("Summer"),
	WINTER("Winter");
	
	private final String value;
	
	MediaSeason(String value){
		this.value = value;
	}
	
	@JsonCreator
	@NotNull
	public static MediaSeason getFromName(@NotNull String value){
		return MediaSeason.valueOf(value);
	}
	
	@Override
	public String toString(){
		return value;
	}
}
