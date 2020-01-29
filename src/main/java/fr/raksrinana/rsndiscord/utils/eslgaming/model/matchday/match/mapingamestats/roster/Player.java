package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.roster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.ISO8601DateDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ISO8601DateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Player{
	@JsonProperty("createdAt")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime createdAt;
	@JsonProperty("updatedAt")
	@JsonDeserialize(using = ISO8601DateTimeDeserializer.class)
	private LocalDateTime updatedAt;
	@JsonProperty("key")
	private String key;
	@JsonProperty("name")
	private String name;
	@JsonProperty("nameOriginal")
	private String nameOriginal;
	@JsonProperty("birthday")
	@JsonDeserialize(using = ISO8601DateDeserializer.class)
	private LocalDate birthday;
	@JsonProperty("gender")
	private String gender;
	@JsonProperty("nick")
	private String nick;
	@JsonProperty("nationality")
	private String nationality;
	@JsonProperty("residence")
	private String residence;
	@JsonProperty("activityStatus")
	private boolean activityStatus;
}
