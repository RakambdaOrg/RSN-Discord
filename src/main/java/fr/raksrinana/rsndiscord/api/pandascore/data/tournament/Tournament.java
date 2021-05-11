package fr.raksrinana.rsndiscord.api.pandascore.data.tournament;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.api.pandascore.data.opponent.OpponentType;
import fr.raksrinana.rsndiscord.utils.json.ISO8601ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Tournament {
	@JsonProperty("begin_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime startDate;
	@JsonProperty("end_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@Nullable
	private ZonedDateTime endDate;
	@JsonProperty("id")
	private int id;
	@JsonProperty("league_id")
	private int leagueId;
	@JsonProperty("live_supported")
	private boolean liveSupported;
	@JsonProperty("modified_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	@NotNull
	private ZonedDateTime modifiedAt;
	@JsonProperty("name")
	@NotNull
	private String name;
	@JsonProperty("prizepool")
	@Nullable
	private String prizepool;
	@JsonProperty("serie_id")
	private int serieId;
	@JsonProperty("slug")
	@Nullable
	private String slug;
	@JsonProperty("winner_id")
	@Nullable
	private Integer winnerId;
	@JsonProperty("winner_type")
	@Nullable
	private OpponentType winnerType;
}
