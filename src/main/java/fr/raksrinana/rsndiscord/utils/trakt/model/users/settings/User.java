package fr.raksrinana.rsndiscord.utils.trakt.model.users.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.ISO8601Deserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class User{
	@JsonProperty("username")
	private String username;
	@JsonProperty("private")
	private boolean userPrivate;
	@JsonProperty("name")
	private String name;
	@JsonProperty("vip")
	private boolean vip;
	@JsonProperty("vip_ep")
	private boolean vipEp;
	@JsonProperty("ids")
	private UserIds ids;
	@JsonProperty("joined_at")
	@JsonDeserialize(using = ISO8601Deserializer.class)
	private LocalDateTime joinedAt;
	@JsonProperty("location")
	private String location;
	@JsonProperty("about")
	private String about;
	@JsonProperty("gender")
	private String gender;
	@JsonProperty("age")
	private int age;
	@JsonProperty("images")
	private UserImages images;
	@JsonProperty("vip_og")
	private boolean vipOg;
	@JsonProperty("vip_years")
	private int vipYears;
}
