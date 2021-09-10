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
public enum MediaRankType{
	RATED("Rated", "❤️"),
	POPULAR("Popular", "⭐");
	
	private final String value;
	private final String icon;
	
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
