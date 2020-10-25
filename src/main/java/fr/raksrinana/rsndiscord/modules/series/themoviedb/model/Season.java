package fr.raksrinana.rsndiscord.modules.series.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Season{
	@JsonProperty("air_date")
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate airDate;
	@JsonProperty("episode_count")
	private int episodeCount;
	@JsonProperty("id")
	private int id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("overview")
	private String overview;
	@JsonProperty("poster_path")
	private String posterPath;
	@JsonProperty("season_number")
	private int seasonNumber;
	
	@Override
	public int hashCode(){
		return Objects.hash(getId());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		Season season = (Season) o;
		return getId() == season.getId();
	}
}
