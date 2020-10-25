package fr.raksrinana.rsndiscord.modules.discordstatus.data.status;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.modules.discordstatus.data.IncidentStatus;
import fr.raksrinana.rsndiscord.modules.discordstatus.data.Page;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class StatusResponse{
	@JsonProperty("page")
	private Page page;
	@JsonProperty("status")
	private IncidentStatus status;
}
