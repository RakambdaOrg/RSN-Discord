package fr.raksrinana.rsndiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class MediaTitle{
	@Getter
	private static final String QUERY = "title {\n" + "userPreferred\n" + "}";
	@JsonProperty("userPreferred")
	private String userPreferred;
}
