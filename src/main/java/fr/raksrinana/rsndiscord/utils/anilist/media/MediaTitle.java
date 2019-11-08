package fr.raksrinana.rsndiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MediaTitle{
	private static final String QUERY = "title {\n" + "userPreferred\n" + "}";
	@JsonProperty("userPreferred")
	private String userPreferred;
	
	public static String getQuery(){
		return QUERY;
	}
	
	@Nonnull
	public String getUserPreferred(){
		return this.userPreferred;
	}
}
