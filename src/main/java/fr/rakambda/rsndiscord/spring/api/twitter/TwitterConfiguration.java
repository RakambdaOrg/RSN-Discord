package fr.rakambda.rsndiscord.spring.api.twitter;

import fr.rakambda.rsndiscord.spring.settings.ApplicationSettings;
import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.signature.TwitterCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwitterConfiguration{
	@Bean
	public TwitterClient getTwitterClient(ApplicationSettings settings){
		var twitterSettings = settings.getTwitter();
		return new TwitterClient(TwitterCredentials.builder()
				.accessToken(twitterSettings.getAccessToken())
				.accessTokenSecret(twitterSettings.getAccessTokenSecret())
				.apiKey(twitterSettings.getApiKey())
				.apiSecretKey(twitterSettings.getApiSecretKey())
				.build());
	}
}
