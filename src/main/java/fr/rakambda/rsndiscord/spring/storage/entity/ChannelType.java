package fr.rakambda.rsndiscord.spring.storage.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ChannelType{
	ANILIST_MEDIA_CHANGE,
	ANILIST_NOTIFICATION,
	AUTO_TODO,
	HERMITCRAFT_LIVE,
	HERMITCRAFT_VIDEO,
	@Deprecated
	RSS,
	TRAKT_MEDIA_CHANGE,
}
