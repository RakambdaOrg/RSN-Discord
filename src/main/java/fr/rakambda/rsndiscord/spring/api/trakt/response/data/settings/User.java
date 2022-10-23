package fr.rakambda.rsndiscord.spring.api.trakt.response.data.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.rsndiscord.spring.json.converter.ISO8601ZonedDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User{
	private String username;
	private boolean userPrivate;
	private String name;
	private boolean vip;
	@JsonProperty("vip_ep")
	private boolean vipEp;
	private UserIds ids;
	@JsonProperty("joined_at")
	@JsonDeserialize(using = ISO8601ZonedDateTimeDeserializer.class)
	private ZonedDateTime joinedAt;
	private String location;
	private String about;
	private String gender;
	private int age;
	private UserImages images;
	@JsonProperty("vip_og")
	private boolean vipOg;
	@JsonProperty("vip_years")
	private int vipYears;
}
