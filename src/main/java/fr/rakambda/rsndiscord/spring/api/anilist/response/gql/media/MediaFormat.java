package fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MediaFormat{
	MANGA("Manga"),
	MOVIE("Movie"),
	MUSIC("Music"),
	NOVEL("Novel"),
	ONA("ONA"),
	ONE_SHOT("One shot"),
	OVA("OVA"),
	SPECIAL("Special"),
	TV("TV"),
	TV_SHORT("TV Short");
	
	private final String value;
}
