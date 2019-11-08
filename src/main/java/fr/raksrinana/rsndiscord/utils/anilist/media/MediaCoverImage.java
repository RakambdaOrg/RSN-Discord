package fr.raksrinana.rsndiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;
import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class MediaCoverImage{
	private static final String QUERY = "coverImage{\n" + "large\n" + "}";
	@JsonProperty("large")
	private URL large;
	
	@Nonnull
	URL getLarge(){
		return this.large;
	}
	
	public static String getQuery(){
		return QUERY;
	}
}
