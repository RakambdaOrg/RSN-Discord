package fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.FuzzyDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = AnimeMedia.class, name = "ANIME"),
		@JsonSubTypes.Type(value = MangaMedia.class, name = "MANGA")
})
public sealed abstract class Media permits AnimeMedia, MangaMedia{
	private FuzzyDate startDate = new FuzzyDate();
	private FuzzyDate endDate = new FuzzyDate();
	private Set<String> genres = new HashSet<>();
	private Set<String> synonyms = new HashSet<>();
	private MediaTitle title;
	private MediaSeason season;
	private MediaFormat format;
	private MediaStatus status;
	@JsonProperty("siteUrl")
	private String url;
	private MediaCoverImage coverImage;
	private MediaSource source;
	private Set<MediaRank> rankings = new HashSet<>();
	@JsonProperty("isAdult")
	private boolean adult;
	private int id;
	
	public abstract MediaType getType();
}
