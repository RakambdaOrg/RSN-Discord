package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote.contestant.tournamentcontestant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote.contestant.tournamentcontestant.squad.ContestantTeam;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote.contestant.tournamentcontestant.squad.GamePlatform;
import fr.raksrinana.rsndiscord.utils.json.ISO8601DateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Squad{
	@JsonProperty("createdAt")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime createdAt;
	@JsonProperty("updatedAt")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime updatedAt;
	@JsonProperty("key")
	private String key;
	@JsonProperty("name")
	private String name;
	@JsonProperty("team")
	private ContestantTeam team;
	@JsonProperty("gamePlatform")
	private GamePlatform gamePlatform;
	@JsonProperty("location")
	private String location;
	@JsonProperty("activityStatus")
	private boolean activityStatus;
	@JsonProperty("description")
	private String description;
	@JsonProperty("teamKey")
	private String teamKey;
}
