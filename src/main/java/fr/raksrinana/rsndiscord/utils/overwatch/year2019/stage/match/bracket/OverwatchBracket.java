package fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.match.bracket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.enums.OverwatchAdvantageComparing;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.enums.OverwatchBracketType;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.enums.OverwatchConclusionStrategy;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.match.bracket.stage.OverwatchBracketStage;
import lombok.Getter;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
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
	
	@Override
	public int hashCode(){
		return Objects.hash(id);
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		OverwatchBracket that = (OverwatchBracket) o;
		return id == that.id;
	}
}
