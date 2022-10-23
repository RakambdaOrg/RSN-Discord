package fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MediaSeason{
	FALL("Fall", 4),
	SPRING("Spring", 2),
	SUMMER("Summer", 3),
	WINTER("Winter", 1);
	
	private final String value;
	private final int index;
}
