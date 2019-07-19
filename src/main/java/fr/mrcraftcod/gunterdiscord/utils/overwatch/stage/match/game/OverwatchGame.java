package fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.mrcraftcod.gunterdiscord.utils.json.OverwatchIDMatchDeserializer;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.enums.OverwatchState;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.enums.OverwatchStatus;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.enums.OverwatchStatusReason;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchGame{
	@JsonProperty("id")
	private int id;
	@JsonProperty("number")
	private int number;
	@JsonProperty("points")
	private List<Integer> points = new ArrayList<>();
	@JsonProperty("attributes")
	private OverwatchGameAttributes attributes;
	@JsonProperty("attributesVersion")
	private String attributesVersion;
	@JsonProperty("state")
	private OverwatchState state;
	@JsonProperty("status")
	private OverwatchStatus status;
	@JsonProperty("statusReason")
	private OverwatchStatusReason statusReason;
	@JsonProperty("stats")
	private String stats; //TODO
	@JsonProperty("handle")
	private String handle;
	@JsonProperty("match")
	@JsonDeserialize(using = OverwatchIDMatchDeserializer.class)
	private int match;
	
	public OverwatchGameAttributes getAttributes(){
		return this.attributes;
	}
	
	public int getNumber(){
		return this.number;
	}
	
	public List<Integer> getPoints(){
		return this.points;
	}
}
