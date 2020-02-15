package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event.banner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.HexColorDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.awt.Color;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Heading{
	@JsonProperty("text")
	private String text;
	@JsonProperty("color")
	@JsonDeserialize(using = HexColorDeserializer.class)
	private Color color;
}
