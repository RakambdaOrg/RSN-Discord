package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event.banner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.utils.Link;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Sponsor{
	@JsonProperty("title")
	private String title;
	@JsonProperty("type")
	private String type;
	@JsonProperty("image")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL image;
	@JsonProperty("link")
	private Link link;
}
