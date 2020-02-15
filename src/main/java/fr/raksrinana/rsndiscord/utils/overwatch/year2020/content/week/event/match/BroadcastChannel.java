package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.utils.Link;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.utils.asset.Asset;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class BroadcastChannel{
	@JsonProperty("title")
	private String title;
	@JsonProperty("colorLogoUrl")
	private Asset colorLogoUrl;
	@JsonProperty("grayLogoUrl")
	private Asset grayLogoUrl;
	@JsonProperty("linkable")
	private boolean linkable;
	@JsonProperty("link")
	private Link link;
}
