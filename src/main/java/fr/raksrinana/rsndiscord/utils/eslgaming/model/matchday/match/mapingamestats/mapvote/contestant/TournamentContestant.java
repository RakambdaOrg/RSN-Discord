package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote.contestant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote.contestant.tournamentcontestant.Squad;
import fr.raksrinana.rsndiscord.utils.json.ISO8601DateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.UnknownDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class TournamentContestant{
	@JsonProperty("createdAt")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime createdAt;
	@JsonProperty("updatedAt")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime updatedAt;
	@JsonProperty("key")
	private String key;
	@JsonProperty("standing")
	@JsonDeserialize(using = UnknownDeserializer.class)
	private Object standing;
	@JsonProperty("squad")
	private Squad squad;
	@JsonProperty("name")
	private String name;
	@JsonProperty("tournamentKey")
	private String tournamentKey;
}
