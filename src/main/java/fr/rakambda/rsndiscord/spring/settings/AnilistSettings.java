package fr.rakambda.rsndiscord.spring.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnilistSettings{
	private String appId;
	private String clientSecret;
	private String redirectUri;
}
