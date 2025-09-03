package fr.rakambda.rsndiscord.spring.api.simkl.response.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.rsndiscord.spring.json.converter.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public sealed abstract class UserHistory permits UserMovieHistory, UserSeriesHistory{
	protected static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
	protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	@JsonProperty("last_watched_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime lastWatchedAt;
	@Nullable
	@JsonProperty("user_rating")
	private Integer userRating;
	@NonNull
	private String status;
	
	@NonNull
	public abstract Long getId();
}
