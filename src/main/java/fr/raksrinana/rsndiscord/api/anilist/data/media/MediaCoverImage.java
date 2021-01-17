package fr.raksrinana.rsndiscord.api.anilist.data.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
class MediaCoverImage{
	public static final String QUERY = """
			coverImage {
				large
			}""";
	
	@JsonProperty("large")
	private URL large;
}
