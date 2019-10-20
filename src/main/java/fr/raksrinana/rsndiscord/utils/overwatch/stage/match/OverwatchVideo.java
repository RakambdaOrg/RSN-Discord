package fr.raksrinana.rsndiscord.utils.overwatch.stage.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchVideo{
	@JsonProperty("name")
	private String name;
	@JsonProperty("description")
	private String description;
	@JsonProperty("videoLink")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL videoLink;
	@JsonProperty("youtubeId")
	private String youtubeId;
	@JsonProperty("thumbnailUrl")
	private URL thumbnailUrl;
}
