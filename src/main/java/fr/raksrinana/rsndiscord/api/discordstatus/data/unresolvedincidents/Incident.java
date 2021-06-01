package fr.raksrinana.rsndiscord.api.discordstatus.data.unresolvedincidents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.api.discordstatus.data.IncidentStatus;
import fr.raksrinana.rsndiscord.api.discordstatus.data.Indicator;
import fr.raksrinana.rsndiscord.utils.json.converter.ISO8601ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.converter.URLDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Incident{
	@JsonProperty("created_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime createdAt;
	@JsonProperty("id")
	private String id;
	@JsonProperty("impact")
	private Indicator impact;
	@JsonProperty("incident_updates")
	private Set<IncidentUpdate> incidentUpdates;
	@JsonProperty("monitoring_at")
	private Object monitoringAt;
	@JsonProperty("name")
	private String name;
	@JsonProperty("page_id")
	private String pageId;
	@JsonProperty("status")
	private IncidentStatus status;
	@JsonProperty("updated_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime updatedAt;
	@JsonProperty("resolved_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime resolvedAt;
	@JsonProperty("shortlink")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL shortLink;
}
