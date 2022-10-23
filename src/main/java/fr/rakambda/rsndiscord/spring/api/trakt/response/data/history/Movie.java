package fr.rakambda.rsndiscord.spring.api.trakt.response.data.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.rsndiscord.spring.json.converter.ISO8601LocalDateDeserializer;
import fr.rakambda.rsndiscord.spring.json.converter.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
	private String title;
	private int year;
	private MediaIds ids;
	private String tagline;
	private String overview;
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate released;
	private int runtime;
	private String country;
	private String trailer;
	private String homepage;
	private String status;
	private double rating;
	private int votes;
	@JsonProperty("comment_count")
	private int commentCount;
	@JsonProperty("updated_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime updatedAt;
	private String language;
	private Set<String> genres;
}
