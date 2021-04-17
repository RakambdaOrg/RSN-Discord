package fr.raksrinana.rsndiscord.api.discordstatus.data.unresolvedincidents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.api.discordstatus.data.Page;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class IncidentsResponse{
	@JsonProperty("page")
	private Page page;
	@JsonProperty("incidents")
	private List<Incident> incidents;
}
