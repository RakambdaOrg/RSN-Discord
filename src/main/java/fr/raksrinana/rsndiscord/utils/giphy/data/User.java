package fr.raksrinana.rsndiscord.utils.giphy.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.URLDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class User{
	@JsonProperty("avatar_url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL avatarUrl;
	@JsonProperty("banner_url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL bannerUrl;
	@JsonProperty("profile_url")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL profileUrl;
	@JsonProperty("username")
	private String username;
	@JsonProperty("display_name")
	private String displayName;
}
