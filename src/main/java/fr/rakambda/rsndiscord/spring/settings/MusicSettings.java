package fr.rakambda.rsndiscord.spring.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicSettings {
	private String youtubeRefreshToken;
	private String spotifyClientId;
	private String spotifyClientSecret;
	private String spotifyCountryCode;
}
