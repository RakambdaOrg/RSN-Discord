package fr.raksrinana.rsndiscord.api.trakt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NonNull;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum TraktMediaType{
	EPISODE("episode"),
	EPISODES("episodes"),
	MOVIE("movie"),
	MOVIES("movies"),
	SEASONS("seasons"),
	SHOWS("shows"),
	UNKNOWN("unknown");
	private final String value;
	
	TraktMediaType(String value){
		this.value = value;
	}
	
	@JsonCreator
	public static TraktMediaType getFromValue(@NonNull final String value){
		for(var type : TraktMediaType.values()){
			if(Objects.equals(value, type.getValue())){
				return type;
			}
		}
		return UNKNOWN;
	}
}
