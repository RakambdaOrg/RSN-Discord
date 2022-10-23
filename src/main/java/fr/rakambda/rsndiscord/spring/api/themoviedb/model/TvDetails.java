package fr.rakambda.rsndiscord.spring.api.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.rsndiscord.spring.json.converter.ISO8601LocalDateDeserializer;
import fr.rakambda.rsndiscord.spring.json.converter.URLDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TvDetails{
	@JsonProperty("backdrop_path")
	private String backdropPath;
	@JsonProperty("created_by")
	private Set<User> createdBy;
	@JsonProperty("episode_run_time")
	private List<Integer> episodeRunTime;
	@JsonProperty("first_air_date")
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate firstAirDate;
	private Set<Genre> genres;
	@JsonDeserialize(using = URLDeserializer.class)
	private URL homepage;
	private int id;
	@JsonProperty("in_production")
	private boolean inProduction;
	private Set<String> languages;
	@JsonProperty("last_air_date")
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate lastAirDate;
	@JsonProperty("last_episode_to_air")
	private Episode lastEpisodeToAir;
	private String name;
	@JsonProperty("next_episode_to_air")
	private Episode nextEpisodeToAir;
	private Set<Network> networks;
	@JsonProperty("number_of_episodes")
	private int numberOfEpisodes;
	@JsonProperty("number_of_seasons")
	private int numberOfSeasons;
	@JsonProperty("origin_country")
	private Set<String> originCountry;
	@JsonProperty("original_language")
	private String originalLanguage;
	@JsonProperty("original_name")
	private String originalName;
	private String overview;
	private long popularity;
	@JsonProperty("poster_path")
	private String posterPath;
	@JsonProperty("production_companies")
	private Set<ProductionCompany> productionCompanies;
	private Set<Season> seasons;
	private String status;
	private String type;
	@JsonProperty("vote_average")
	private double voteAverage;
	@JsonProperty("vote_count")
	private int voteCount;
}
