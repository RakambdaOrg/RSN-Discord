package fr.raksrinana.rsndiscord.api.pandascore.data.serie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.api.pandascore.data.opponent.OpponentType;
import fr.raksrinana.rsndiscord.utils.json.converter.ISO8601ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.converter.ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Serie{
	@JsonProperty("begin_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime startDate;
	@JsonProperty("description")
	@Nullable
	private String description;
	@JsonProperty("end_at")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime endDate;
	@JsonProperty("full_name")
	@NotNull
	private String fullName;
	@JsonProperty("id")
	private int id;
	@JsonProperty("league_id")
	private int leagueId;
	@JsonProperty("modified_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime modifiedAt;
	@JsonProperty("name")
	@Nullable
	private String name;
	@JsonProperty("season")
	@Nullable
	private String season;
	@JsonProperty("slug")
	@Nullable
	private String slug;
	@JsonProperty("tier")
	@Nullable
	private String tier;
	@JsonProperty("winner_id")
	@Nullable
	private Integer winnerId;
	@JsonProperty("winner_type")
	@Nullable
	private OpponentType winnerType;
	@JsonProperty("year")
	private int year;
}
