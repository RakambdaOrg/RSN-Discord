package fr.raksrinana.rsndiscord.api.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NonNull;
import static java.util.Objects.isNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum MediaFormat{
	MANGA("Manga"),
	MOVIE("Movie"),
	MUSIC("Music"),
	NOVEL("Novel"),
	ONA,
	ONE_SHOT("One shot"),
	OVA,
	SPECIAL("Special"),
	TV("TV"),
	TV_SHORT("TV Short");
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
		return isNull(this.display) ? this.name() : this.display;
	}
}
