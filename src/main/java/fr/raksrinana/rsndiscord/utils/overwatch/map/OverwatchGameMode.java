package fr.raksrinana.rsndiscord.utils.overwatch.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchGameMode{
	@JsonProperty("Id")
	private String id;
	@JsonProperty("name")
	private String name;
}
