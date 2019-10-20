package fr.raksrinana.rsndiscord.utils.overwatch.stage.match.bracket.stage.tournament;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.OverwatchIDSeriesDeserializer;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchBracketTournament{
	@JsonProperty("id")
	private int id;
	@JsonProperty("availableLanguages")
	private List<String> availableLanguages;
	@JsonProperty("game")
	private String game;
	@JsonProperty("featured")
	private boolean featured;
	@JsonProperty("draft")
	private boolean draft;
	@JsonProperty("handle")
	private String handle;
	@JsonProperty("title")
	private String title;
	@JsonProperty("series")
	@JsonDeserialize(using = OverwatchIDSeriesDeserializer.class)
	private int series;
}
