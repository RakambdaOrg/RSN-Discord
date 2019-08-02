package fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.tournament;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.enums.OverwatchTournamentType;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchTournament{
	@JsonProperty("id")
	private int id;
	@JsonProperty("type")
	private OverwatchTournamentType type;
	
	@Override
	public boolean equals(final Object obj){
		return obj instanceof OverwatchTournament && Objects.equals(this.getId(), ((OverwatchTournament) obj).getId());
	}
	
	private int getId(){
		return this.id;
	}
	
	public OverwatchTournamentType getType(){
		return this.type;
	}
}
