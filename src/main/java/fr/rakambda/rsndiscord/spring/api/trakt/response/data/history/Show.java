package fr.rakambda.rsndiscord.spring.api.trakt.response.data.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.rsndiscord.spring.json.converter.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import java.time.ZonedDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Show {
	private String title;
	private int year;
	private MediaIds ids;
	@Nullable
	private String overview;
	@JsonProperty("first_aired")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime firstAired;
	private Airing airs;
	private int runtime;
	private String country;
	private String trailer;
	private String homepage;
	private String status;
	private double rating;
	private int votes;
	@JsonProperty("comment_count")
	private int commentCount;
	private String network;
	@JsonProperty("updated_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime updatedAt;
	private String language;
	private Set<String> genres;
	@JsonProperty("aired_episodes")
	private int airedEpisodes;
}
