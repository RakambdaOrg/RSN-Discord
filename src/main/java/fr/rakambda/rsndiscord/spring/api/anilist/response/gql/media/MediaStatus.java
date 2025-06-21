package fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MediaStatus{
	CANCELLED("Cancelled"),
	FINISHED("Finished"),
	NOT_YET_RELEASED("Not yet released"),
	RELEASING("Releasing"),
	HIATUS("Hiatus");
	
	private final String value;
}
