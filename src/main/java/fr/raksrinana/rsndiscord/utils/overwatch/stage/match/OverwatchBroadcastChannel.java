package fr.raksrinana.rsndiscord.utils.overwatch.stage.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchBroadcastChannel{
	@JsonProperty("id")
	private String id;
	@JsonProperty("url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL url;
}
