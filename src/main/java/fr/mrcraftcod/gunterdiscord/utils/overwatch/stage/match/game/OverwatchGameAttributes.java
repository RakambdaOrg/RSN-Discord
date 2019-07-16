package fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchGameAttributes{
	@JsonProperty("build")
	private int build;
	@JsonProperty("instanceID")
	private String instanceID;
	@JsonProperty("map")
	private String map;
	@JsonProperty("mapGuid")
	private String mapGuid;
	@JsonProperty("mapScore")
	private Map<String, Integer> mapScore;
}
