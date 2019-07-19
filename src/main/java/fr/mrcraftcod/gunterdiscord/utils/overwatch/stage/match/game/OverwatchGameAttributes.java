package fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.OverwatchMap;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.OverwatchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchGameAttributes{
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchGameAttributes.class);
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
	
	public String getMapName(){
		return this.getMapObject().map(Object::toString).or(() -> {
			LOGGER.warn("Map guid not found: {} ({})", this.getMapGuid(), this.getMap());
			return Optional.ofNullable(this.getMap());
		}).orElse(null);
	}
	
	public Optional<OverwatchMap> getMapObject(){
		return OverwatchUtils.getMap(this.getMapGuid());
	}
	
	private String getMapGuid(){
		return this.mapGuid;
	}
	
	public String getMap(){
		return this.map;
	}
}
