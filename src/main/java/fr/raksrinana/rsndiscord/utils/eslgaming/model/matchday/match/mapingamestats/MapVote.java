package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.generic.GameVersionMap;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote.Action;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote.Contestant;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.mapvote.Round;
import fr.raksrinana.rsndiscord.utils.json.ISO8601DateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class MapVote{
	@JsonProperty("createdAt")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime createdAt;
	@JsonProperty("updatedAt")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime updatedAt;
	@JsonProperty("key")
	private String key;
	@JsonProperty("step")
	private int step;
	@JsonProperty("contestant")
	private Contestant contestant;
	@JsonProperty("action")
	private Action action;
	@JsonProperty("gameVersionMap")
	private GameVersionMap gameVersionMap;
	@JsonProperty("round")
	private Round round;
	@JsonProperty("matchKey")
	private String matchKey;
}
