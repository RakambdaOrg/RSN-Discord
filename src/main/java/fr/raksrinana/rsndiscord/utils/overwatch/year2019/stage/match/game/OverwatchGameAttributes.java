package fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.match.game;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.OverwatchMap;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.OverwatchUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
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
	
	public Optional<String> getMapName(){
		return this.getMapObject().map(Object::toString).or(() -> {
			LOGGER.warn("Map guid not found: {} ({})", this.getMapGuid(), this.getMap());
			return Optional.ofNullable(this.getMap());
		});
	}
	
	public Optional<OverwatchMap> getMapObject(){
		return OverwatchUtils.getMap(this.getMapGuid());
	}
}
