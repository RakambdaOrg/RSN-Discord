package fr.rakambda.rsndiscord.spring.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TwitterSettings{
	private String accessToken;
	private String accessTokenSecret;
	private String apiKey;
	private String apiSecretKey;
}
