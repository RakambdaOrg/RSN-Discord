package fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.mapingamestats.roster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ISO8601ZonedDateTimeDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Player{
	@JsonProperty("createdAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime createdAt;
	@JsonProperty("updatedAt")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime updatedAt;
	@JsonProperty("key")
	private String key;
	@JsonProperty("name")
	private String name;
	@JsonProperty("nameOriginal")
	private String nameOriginal;
	@JsonProperty("birthday")
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
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
