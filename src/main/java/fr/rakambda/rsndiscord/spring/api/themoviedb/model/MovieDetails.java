package fr.rakambda.rsndiscord.spring.api.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.rsndiscord.spring.json.converter.ISO8601LocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDetails{
	private boolean adult;
	@JsonProperty("backdrop_path")
	private String backdropPath;
	@JsonProperty("belongs_to_collection")
	private Collection belongsToCollection;
	private int budget;
	private Set<Genre> genres;
	private String homepage;
	private int id;
	@JsonProperty("imdb_id")
	private String imdbId;
	@JsonProperty("original_language")
	private String originalLanguage;
	@JsonProperty("original_title")
	private String originalTitle;
	private String overview;
	private long popularity;
	@JsonProperty("poster_path")
	private String posterPath;
	@JsonProperty("production_companies")
	private Set<ProductionCompany> productionCompanies;
	@JsonProperty("production_countries")
	private Set<ProductionCountry> productionCountries;
	@JsonProperty("release_date")
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate releaseDate;
	private int revenue;
	private int runtime;
	@JsonProperty("spoken_languages")
	private Set<Language> spokenLanguages;
	private String status;
	private String tagline;
	private String title;
	private boolean video;
	@JsonProperty("vote_average")
	private double voteAverage;
	@JsonProperty("vote_count")
	private int voteCount;
}
