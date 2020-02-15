package fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.tournament;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.enums.OverwatchTournamentType;
import lombok.Getter;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class OverwatchTournament{
	@JsonProperty("id")
	private int id;
	@JsonProperty("type")
	private OverwatchTournamentType type;
	
	@Override
	public boolean equals(final Object obj){
		return obj instanceof OverwatchTournament && Objects.equals(this.getId(), ((OverwatchTournament) obj).getId());
	}
	
	@Override
	public int hashCode(){
		return Objects.hash(id);
	}
}
