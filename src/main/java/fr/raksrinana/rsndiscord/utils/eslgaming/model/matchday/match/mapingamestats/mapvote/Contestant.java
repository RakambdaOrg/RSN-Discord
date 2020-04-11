package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote.contestant.GameResult;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote.contestant.TournamentContestant;
import fr.raksrinana.rsndiscord.utils.json.ISO8601ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.UnknownDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Contestant{
	@JsonProperty("createdAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime createdAt;
	@JsonProperty("updatedAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime updatedAt;
	@JsonProperty("key")
	private String key;
	@JsonProperty("tournamentContestant")
	private TournamentContestant tournamentContestant;
	@JsonProperty("gameResult")
	private GameResult gameResult;
	@JsonProperty("score")
	private int score;
	@JsonProperty("position")
	private int position;
	@JsonProperty("matchKey")
	private String matchKey;
	@JsonProperty("rank")
	@JsonDeserialize(using = UnknownDeserializer.class)
	private Object rank;
	@JsonProperty("points")
	@JsonDeserialize(using = UnknownDeserializer.class)
	private Object points;
}
