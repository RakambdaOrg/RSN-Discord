package fr.raksrinana.rsndiscord.api.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequiredArgsConstructor
@Getter
public enum MediaSeason{
	FALL("Fall", 4),
	SPRING("Spring", 2),
	SUMMER("Summer", 3),
	WINTER("Winter", 1);
	
	private final String value;
	private final int index;
	
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
