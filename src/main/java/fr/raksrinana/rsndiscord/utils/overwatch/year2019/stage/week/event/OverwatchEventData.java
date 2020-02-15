package fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.week.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import lombok.Getter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class OverwatchEventData{
	@JsonProperty("titles")
	private final List<String> titles = new ArrayList<>();
	@JsonProperty("locationText")
	private String locationText;
	@JsonProperty("locationUrl")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL locationUrl;
	@JsonProperty("descriptionUrl")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL descriptionUrl;
	@JsonProperty("imageUrl")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL imageUrl;
	@JsonProperty("hostTeamId")
	private String hostTeamId;
	@JsonProperty("hostTeamColorUsage")
	private String hostTeamColorUsage;
	@JsonProperty("partner")
	private OverwatchEventPartner partner;
}
