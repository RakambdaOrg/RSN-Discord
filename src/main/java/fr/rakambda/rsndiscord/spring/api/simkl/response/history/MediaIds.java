package fr.rakambda.rsndiscord.spring.api.simkl.response.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaIds {
	@NonNull
	private Long simkl;
	@Nullable
	private String slug;
	@Nullable
	private String imdb;
	@Nullable
	private Long tmdb;
	@Nullable
	private String zap2it;
	@Nullable
	private String offen;
	@Nullable
	private String tvdb;
	@Nullable
	private String mal;
	@Nullable
	private String anidb;
	@Nullable
	private String fb;
	@Nullable
	private String instagram;
	@Nullable
	private String wikijp;
	@Nullable
	private String ann;
	@Nullable
	private String offjp;
	@Nullable
	private String wikien;
	@Nullable
	private String allcin;
	@Nullable
	private String tw;
	@Nullable
	private String tvdbslug;
	@Nullable
	private String crunchyroll;
	@Nullable
	private String anilist;
	@Nullable
	private String animeplanet;
	@Nullable
	private String anisearch;
	@Nullable
	private String kitsu;
	@Nullable
	private String livechart;
	@Nullable
	private String traktslug;
}
