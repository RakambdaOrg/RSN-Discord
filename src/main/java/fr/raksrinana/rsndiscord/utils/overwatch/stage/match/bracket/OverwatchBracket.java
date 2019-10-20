package fr.raksrinana.rsndiscord.utils.overwatch.stage.match.bracket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.overwatch.enums.OverwatchAdvantageComparing;
import fr.raksrinana.rsndiscord.utils.overwatch.enums.OverwatchBracketType;
import fr.raksrinana.rsndiscord.utils.overwatch.enums.OverwatchConclusionStrategy;
import fr.raksrinana.rsndiscord.utils.overwatch.stage.match.bracket.stage.OverwatchBracketStage;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchBracket{
	@JsonProperty("id")
	private int id;
	@JsonProperty("matchConclusionValue")
	private int matchConclusionValue;
	@JsonProperty("matchConclusionStrategy")
	private OverwatchConclusionStrategy matchConclusionStrategy;
	@JsonProperty("winners")
	private int winners;
	@JsonProperty("teamSize")
	private int teamSize;
	@JsonProperty("repeatableMatchUps")
	private int repeatableMatchUps;
	@JsonProperty("stage")
	private OverwatchBracketStage stage;
	@JsonProperty("type")
	private OverwatchBracketType type;
	@JsonProperty("clientHints")
	private List<String> clientHints; //TODO
	@JsonProperty("advantageComparing")
	private OverwatchAdvantageComparing advantageComparing;
	@JsonProperty("thirdPlaceMatch")
	private boolean thirdPlaceMatch;
	@JsonProperty("allowDraw")
	private boolean allowDraw;
}
