package fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.match.bracket.stage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.match.bracket.stage.tournament.OverwatchBracketTournament;
import lombok.Getter;
import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class OverwatchBracketStage{
	@JsonProperty("id")
	private int id;
	@JsonProperty("availableLanguages")
	private Set<String> availableLanguages;
	@JsonProperty("tournament")
	private OverwatchBracketTournament tournament;
	
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
		OverwatchBracketStage that = (OverwatchBracketStage) o;
		return id == that.id;
	}
}
