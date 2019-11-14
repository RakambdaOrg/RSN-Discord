package fr.raksrinana.rsndiscord.utils.overwatch.stage.match.bracket.stage.tournament;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.OverwatchIDSeriesDeserializer;
import lombok.Getter;
import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class OverwatchBracketTournament{
	@JsonProperty("id")
	private int id;
	@JsonProperty("availableLanguages")
	private Set<String> availableLanguages;
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
		OverwatchBracketTournament that = (OverwatchBracketTournament) o;
		return id == that.id;
	}
}
