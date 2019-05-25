package fr.mrcraftcod.gunterdiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URL;

class AniListCoverImage{
	@JsonProperty("large")
	private URL large;
	
	URL getLarge(){
		return this.large;
	}
}
