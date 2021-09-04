package fr.raksrinana.rsndiscord.api.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum MediaSource{
	ORIGINAL("Original"),
	MANGA("Manga"),
	LIGHT_NOVEL("Light novel"),
	VISUAL_NOVEL("Visual novel"),
	VIDEO_GAME("Video game"),
	OTHER("Other"),
	NOVEL("Novel"),
	DOUJINSHI("Doujinshi"),
	ANIME("Anime"),
	WEB_NOVEL("Web novel"),
	LIVE_ACTION("Live action"),
	GAME("Game"),
	COMIC("Comic"),
	MULTIMEDIA_PROJECT("Multimedia project"),
	PICTURE_BOOK("Picture book");
	
	private final String value;
	
	MediaSource(String value){
		this.value = value;
	}
	
	@JsonCreator
	@NotNull
	public static MediaSource getFromName(@NotNull String value){
		return MediaSource.valueOf(value);
	}
	
	@Override
	public String toString(){
		return value;
	}
}
