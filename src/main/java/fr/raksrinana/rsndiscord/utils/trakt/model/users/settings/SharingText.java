package fr.raksrinana.rsndiscord.utils.trakt.model.users.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class SharingText{
	@JsonProperty("watching")
	private String watching;
	@JsonProperty("watched")
	private String watched;
	@JsonProperty("rated")
	private String rated;
}
