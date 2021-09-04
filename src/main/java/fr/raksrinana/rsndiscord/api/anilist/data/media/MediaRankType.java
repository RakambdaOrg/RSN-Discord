package fr.raksrinana.rsndiscord.api.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum MediaRankType{
	RATED("Rated"),
	POPULAR("Popular");
	
	private final String value;
	
	MediaRankType(String value){
		this.value = value;
	}
	
	@JsonCreator
	@NotNull
	public static MediaRankType getFromName(@NotNull String value){
		return MediaRankType.valueOf(value);
	}
	
	@Override
	public String toString(){
		return value;
	}
}
