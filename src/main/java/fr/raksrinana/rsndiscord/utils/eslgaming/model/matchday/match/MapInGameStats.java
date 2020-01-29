package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.MapVote;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.Roster;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.RoundStats;
import fr.raksrinana.rsndiscord.utils.json.UnknownDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class MapInGameStats{
	@JsonProperty("meta")
	@JsonDeserialize(using = UnknownDeserializer.class)
	private Object meta;
	@JsonProperty("contestants")
	@JsonDeserialize(using = UnknownDeserializer.class)
	private Object contestants;
	@JsonProperty("subrounds")
	@JsonDeserialize(using = UnknownDeserializer.class)
	private Object subRounds;
	@JsonProperty("mapvotes")
	private List<MapVote> mapVotes;
	@JsonProperty("roster")
	private Map<Integer, List<Roster>> roster;
	@JsonProperty("matchroundstats")
	private Map<Integer, Map<String, RoundStats>> matchRoundStats;
	@JsonProperty("matchroundstatslately")
	private Map<Integer, Map<String, RoundStats>> matchRoundStatsLately;
}
