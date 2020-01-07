package fr.raksrinana.rsndiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NonNull;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum MediaFormat{
	TV("TV"), TV_SHORT("TV Short"), MOVIE("Movie"), SPECIAL("Special"), OVA, ONA, MUSIC("Music"), MANGA("Manga"), NOVEL("Novel"), ONE_SHOT("One shot");
	private final String display;
	
	MediaFormat(){
		this(null);
	}
	
	MediaFormat(final String display){
		this.display = display;
	}
	
	@JsonCreator
	@NonNull
	public static MediaFormat getFromString(@NonNull final String value){
		return MediaFormat.valueOf(value);
	}
	
	@Override
	public String toString(){
		return Objects.isNull(this.display) ? this.name() : this.display;
	}
}
