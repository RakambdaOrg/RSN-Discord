package fr.raksrinana.rsndiscord.api.trakt.model.users.settings;

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
public class Account{
	@JsonProperty("timezone")
	private String timezone;
	@JsonProperty("date_format")
	private String dateFormat;
	@JsonProperty("time_24h")
	private boolean time24h;
	@JsonProperty("cover_image")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL cover_image;
}
