package fr.raksrinana.rsndiscord.utils.trakt.model.users.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class MediaIds{
	@JsonProperty("trakt")
	private Long trakt;
	@JsonProperty("slug")
	private String slug;
	@JsonProperty("imdb")
	private String imdb;
	@JsonProperty("tmdb")
	private Long tmdb;
	
	@Override
	public int hashCode(){
		return Objects.hash(getTrakt());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		MediaIds mediaIds = (MediaIds) o;
		return Objects.equals(getTrakt(), mediaIds.getTrakt());
	}
}
