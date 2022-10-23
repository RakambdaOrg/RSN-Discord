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
public class Episode{
	@JsonProperty("air_date")
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate airDate;
	@JsonProperty("episode_number")
	private int episodeNumber;
	private int id;
	private String name;
	private String overview;
	@JsonProperty("production_code")
	private String productionCode;
	@JsonProperty("season_number")
	private int seasonNumber;
	@JsonProperty("show_id")
	private int showId;
	@JsonProperty("still_path")
	private String stillPath;
	@JsonProperty("vote_average")
	private double voteAverage;
	@JsonProperty("vote_count")
	private int voteCount;
}
