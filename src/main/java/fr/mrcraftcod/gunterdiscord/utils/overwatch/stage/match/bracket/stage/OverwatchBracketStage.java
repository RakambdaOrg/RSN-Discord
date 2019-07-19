package fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match.bracket.stage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match.bracket.stage.tournament.OverwatchBracketTournament;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchBracketStage{
	@JsonProperty("id")
	private int id;
	@JsonProperty("availableLanguages")
	private List<String> availableLanguages;
	@JsonProperty("tournament")
	private OverwatchBracketTournament tournament;
}
