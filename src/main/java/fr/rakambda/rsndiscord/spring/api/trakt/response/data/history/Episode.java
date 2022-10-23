package fr.rakambda.rsndiscord.spring.api.trakt.response.data.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.rsndiscord.spring.json.converter.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Episode {
	private int season;
	private int number;
	private String title;
	private MediaIds ids;
	private String overview;
	private double rating;
	private int votes;
	@JsonProperty("comment_count")
	private int commentCount;
	@JsonProperty("first_aired")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime firstAired;
	@JsonProperty("updated_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime updatedAt;
	private int runtime;
}
