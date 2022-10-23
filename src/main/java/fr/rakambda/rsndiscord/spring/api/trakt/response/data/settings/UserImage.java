package fr.rakambda.rsndiscord.spring.api.trakt.response.data.settings;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.rakambda.rsndiscord.spring.json.converter.URLDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.net.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserImage{
	@JsonDeserialize(using = URLDeserializer.class)
	private URL full;
}
