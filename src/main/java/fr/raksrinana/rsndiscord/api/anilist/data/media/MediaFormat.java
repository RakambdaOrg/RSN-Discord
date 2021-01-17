package fr.raksrinana.rsndiscord.api.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.NotNull;
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
	
	private final String value;
	
	MediaFormat(){
		this(null);
	}
	
	MediaFormat(String value){
		this.value = value;
	}
	
	@JsonCreator
	@NotNull
	public static MediaFormat getFromName(@NotNull String value){
		return MediaFormat.valueOf(value);
	}
	
	@Override
	public String toString(){
		return isNull(value) ? name() : value;
	}
}
