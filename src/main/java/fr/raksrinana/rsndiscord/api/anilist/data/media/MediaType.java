package fr.raksrinana.rsndiscord.api.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum MediaType{
	ANIME(true, "Anime"),
	MANGA(false, "Manga");
	private final boolean shouldDisplay;
	private final String display;
	
	MediaType(final boolean shouldDisplay, final String display){
		this.shouldDisplay = shouldDisplay;
		this.display = display;
	}
	
	@JsonCreator
	@NonNull
	public static MediaType getFromString(@NonNull final String value){
		return MediaType.valueOf(value);
	}
	
	@Override
	public String toString(){
		return this.display;
	}
}
