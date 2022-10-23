package fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MediaRankType{
	RATED("Rated", "❤️"),
	POPULAR("Popular", "⭐");
	
	private final String value;
	private final String icon;
}
