package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.generic.GameVersionMap;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.roundstats.Stats;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class RoundStats{
	@JsonProperty("gameVersionMap")
	private GameVersionMap gameVersionMap;
	@JsonProperty("stats")
	private Stats stats;
	@JsonProperty("average")
	private Stats average;
}
