package fr.raksrinana.rsndiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.annotation.Nonnull;
import java.net.URL;

class AniListCoverImage{
	@JsonProperty("large")
	private URL large;
	
	@Nonnull
	URL getLarge(){
		return this.large;
	}
}
