package fr.raksrinana.rsndiscord.api.trakt.model.users.history;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Airing{
	@JsonProperty("day")
	private String day;
	@JsonProperty("time")
	private String time;
	@JsonProperty("timezone")
	private String timezone;
}
