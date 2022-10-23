package fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MediaType{
	ANIME(true, "Anime"),
	MANGA(false, "Manga");
	
	private final boolean shouldDisplay;
	private final String value;
}
