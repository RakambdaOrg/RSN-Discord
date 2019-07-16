package fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.tournament;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.enums.OverwatchTournamentType;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchTournaments{
	@JsonProperty("id")
	private int id;
	@JsonProperty("type")
	private OverwatchTournamentType type;
}
