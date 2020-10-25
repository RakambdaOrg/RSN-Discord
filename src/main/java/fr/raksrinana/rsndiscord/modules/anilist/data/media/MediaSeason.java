package fr.raksrinana.rsndiscord.modules.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum MediaSeason{
	WINTER("Winter"), SPRING("Spring"), SUMMER("Summer"), FALL("Fall");
	private final String display;
	
	MediaSeason(final String display){
		this.display = display;
	}
	
	@JsonCreator
	@NonNull
	public static MediaSeason getFromString(@NonNull final String value){
		return MediaSeason.valueOf(value);
	}
	
	@Override
	public String toString(){
		return this.display;
	}
}
