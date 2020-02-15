package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event.banner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.utils.Link;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Venue{
	@JsonProperty("title")
	private String title;
	@JsonProperty("link")
	private Link link;
	@JsonProperty("name")
	private String name;
	@JsonProperty("location")
	private String location;
}
