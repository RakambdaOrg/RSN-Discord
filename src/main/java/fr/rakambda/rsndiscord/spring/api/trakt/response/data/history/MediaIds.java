package fr.rakambda.rsndiscord.spring.api.trakt.response.data.history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaIds{
	private Long trakt;
	private String slug;
	private String imdb;
	private Long tmdb;
}
