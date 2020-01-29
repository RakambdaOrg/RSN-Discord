package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.roundstats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.roundstats.stats.Maps;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.roundstats.stats.Scores;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Stats{
	@JsonProperty("maps")
	private Maps maps;
	@JsonProperty("scores")
	private Scores scores;
}
