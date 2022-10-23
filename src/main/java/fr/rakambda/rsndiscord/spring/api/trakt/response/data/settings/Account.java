package fr.rakambda.rsndiscord.spring.api.trakt.response.data.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.rsndiscord.spring.json.converter.URLDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.net.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account{
	private String timezone;
	@JsonProperty("date_format")
	private String dateFormat;
	@JsonProperty("time_24h")
	private boolean time24h;
	@JsonProperty("cover_image")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL coverImage;
}
