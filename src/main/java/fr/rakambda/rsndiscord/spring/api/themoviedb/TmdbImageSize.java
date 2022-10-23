package fr.rakambda.rsndiscord.spring.api.themoviedb;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TmdbImageSize{
	ORIGINAL("original");
	
	private final String value;
}
