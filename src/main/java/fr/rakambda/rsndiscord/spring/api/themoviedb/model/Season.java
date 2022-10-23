package fr.rakambda.rsndiscord.spring.api.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.rsndiscord.spring.json.converter.ISO8601LocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Season{
	@JsonProperty("air_date")
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate airDate;
	@JsonProperty("episode_count")
	private int episodeCount;
	private int id;
	private String name;
	private String overview;
	@JsonProperty("poster_path")
	private String posterPath;
	@JsonProperty("season_number")
	private int seasonNumber;
}
