package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Vod{
	@JsonProperty("url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL url;
}
