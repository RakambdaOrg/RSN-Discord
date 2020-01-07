package fr.raksrinana.rsndiscord.utils.trakt.model.users.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class Connections{
	@JsonProperty("facebook")
	private boolean facebook;
	@JsonProperty("twitter")
	private boolean twitter;
	@JsonProperty("google")
	private boolean google;
	@JsonProperty("tumblr")
	private boolean tumblr;
	@JsonProperty("medium")
	private boolean medium;
	@JsonProperty("slack")
	private boolean slack;
}
