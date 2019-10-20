package fr.raksrinana.rsndiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class AniListMediaTitle{
	@JsonProperty("userPreferred")
	private String userPreferred;
	
	@Nonnull
	String getUserPreferred(){
		return this.userPreferred;
	}
}
