package fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.match.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.OverwatchIDMatchDeserializer;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.enums.OverwatchState;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.enums.OverwatchStatus;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.enums.OverwatchStatusReason;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("FieldMayBeFinal")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
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
		OverwatchGame that = (OverwatchGame) o;
		return id == that.id;
	}
}
