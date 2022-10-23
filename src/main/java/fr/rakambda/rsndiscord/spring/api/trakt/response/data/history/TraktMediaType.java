package fr.rakambda.rsndiscord.spring.api.trakt.response.data.history;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TraktMediaType{
	EPISODE("episode"),
	EPISODES("episodes"),
	MOVIE("movie"),
	MOVIES("movies"),
	SEASONS("seasons"),
	SHOWS("shows"),
	UNKNOWN("unknown");
	
	private final String value;
}
