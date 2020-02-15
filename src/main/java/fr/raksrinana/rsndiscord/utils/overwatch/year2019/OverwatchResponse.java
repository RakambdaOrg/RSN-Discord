package fr.raksrinana.rsndiscord.utils.overwatch.year2019;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class OverwatchResponse{
	@JsonProperty("meta")
	private OverwatchMeta meta;
	@JsonProperty("data")
	private OverwatchData data;
}
