package fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchScore implements Comparable<OverwatchScore>{
	@JsonProperty("value")
	private int value;
	
	@Override
	public int compareTo(@Nonnull final OverwatchScore overwatchScore){
		return Integer.compare(this.getValue(), overwatchScore.getValue());
	}
	
	public int getValue(){
		return this.value;
	}
}
