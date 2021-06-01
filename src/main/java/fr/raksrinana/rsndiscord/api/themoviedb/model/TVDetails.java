package fr.raksrinana.rsndiscord.api.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.api.themoviedb.TheMovieDBApi;
import fr.raksrinana.rsndiscord.utils.json.converter.ISO8601LocalDateDeserializer;
import fr.raksrinana.rsndiscord.utils.json.converter.URLDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class TVDetails implements MediaDetails{
	@JsonProperty("backdrop_path")
	private String backdropPath;
	@JsonProperty("created_by")
	private Set<User> createdBy;
	@JsonProperty("episode_run_time")
	private List<Integer> episodeRunTime;
	@JsonProperty("first_air_date")
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate firstAirDate;
	@JsonProperty("genres")
	private Set<Genre> genres;
	@JsonProperty("homepage")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL homepage;
	@JsonProperty("id")
	private int id;
	@JsonProperty("in_production")
	private boolean inProduction;
	@JsonProperty("languages")
	private Set<String> languages;
	@JsonProperty("last_air_date")
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate lastAirDate;
	@JsonProperty("last_episode_to_air")
	private Episode lastEpisodeToAir;
	@JsonProperty("name")
	private String name;
	@JsonProperty("next_episode_to_air")
	private Episode nextEpisodeToAir;
	@JsonProperty("networks")
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
	@JsonProperty("overview")
	private String overview;
	@JsonProperty("popularity")
	private long popularity;
	@JsonProperty("poster_path")
	private String posterPath;
	@JsonProperty("production_companies")
	private Set<ProductionCompany> productionCompanies;
	@JsonProperty("seasons")
	private Set<Season> seasons;
	@JsonProperty("status")
	private String status;
	@JsonProperty("type")
	private String type;
	@JsonProperty("vote_average")
	private double voteAverage;
	@JsonProperty("vote_count")
	private int voteCount;
	
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
		TVDetails tvDetails = (TVDetails) o;
		return getId() == tvDetails.getId();
	}
	
	@Override
	@NotNull
	public Optional<URL> getPosterURL(int seasonNumber){
		return getSeason(seasonNumber).map(season -> TheMovieDBApi.getImageURL(season.getPosterPath(), "original"));
	}
	
	@NotNull
	public Optional<Season> getSeason(int seasonNumber){
		return getSeasons().stream().filter(season -> Objects.equals(season.getSeasonNumber(), seasonNumber)).findFirst();
	}
	
	@Override
	@NotNull
	public Optional<URL> getPosterURL(){
		return ofNullable(getPosterPath()).map(path -> TheMovieDBApi.getImageURL(path, "original"));
	}
}
