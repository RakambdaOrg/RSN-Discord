package fr.raksrinana.rsndiscord.api.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum MediaType{
	ANIME(true, "Anime"),
	MANGA(false, "Manga");
	
	private final boolean shouldDisplay;
	private final String value;
	
	MediaType(boolean shouldDisplay, String value){
		this.shouldDisplay = shouldDisplay;
		this.value = value;
	}
	
	@JsonCreator
	@NotNull
	public static MediaType getFromName(@NotNull String value){
		return MediaType.valueOf(value);
	}
	
	@Override
	public String toString(){
		return value;
	}
}
