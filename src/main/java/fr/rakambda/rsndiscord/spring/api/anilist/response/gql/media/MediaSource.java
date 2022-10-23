package fr.rakambda.rsndiscord.spring.api.anilist.response.gql.media;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MediaSource{
	ORIGINAL("Original"),
	MANGA("Manga"),
	LIGHT_NOVEL("Light novel"),
	VISUAL_NOVEL("Visual novel"),
	VIDEO_GAME("Video game"),
	OTHER("Other"),
	NOVEL("Novel"),
	DOUJINSHI("Doujinshi"),
	ANIME("Anime"),
	WEB_NOVEL("Web novel"),
	LIVE_ACTION("Live action"),
	GAME("Game"),
	COMIC("Comic"),
	MULTIMEDIA_PROJECT("Multimedia project"),
	PICTURE_BOOK("Picture book");
	
	private final String value;
}
