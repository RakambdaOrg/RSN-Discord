package fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.week.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.overwatch.year2019.enums.OverwatchEventType;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class OverwatchEvent{
	@JsonProperty("type")
	private OverwatchEventType type;
	@JsonProperty("data")
	private OverwatchEventData data;
}