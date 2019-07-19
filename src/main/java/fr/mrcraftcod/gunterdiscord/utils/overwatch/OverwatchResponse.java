package fr.mrcraftcod.gunterdiscord.utils.overwatch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchResponse{
	@JsonProperty("meta")
	private OverwatchMeta meta;
	@JsonProperty("data")
	private OverwatchData data;
	
	@Nonnull
	public OverwatchData getData(){
		return this.data;
	}
}
