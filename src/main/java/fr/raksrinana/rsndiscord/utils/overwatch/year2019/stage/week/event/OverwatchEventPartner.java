package fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.week.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import lombok.Getter;
import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class OverwatchEventPartner{
	@JsonProperty("title")
	private String title;
	@JsonProperty("website")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL website;
}
