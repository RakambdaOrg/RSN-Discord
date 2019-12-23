package fr.raksrinana.rsndiscord.utils.trakt.model.users.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class MediaIds{
	@JsonProperty("trakt")
	private long trakt;
	@JsonProperty("slug")
	private String slug;
	@JsonProperty("imdb")
	private String imdb;
	@JsonProperty("tmdb")
	private long tmdb;
}
