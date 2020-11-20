package fr.raksrinana.rsndiscord.modules.twitter;

import com.github.redouane59.twitter.ITwitterClientV1;
import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.dto.tweet.Tweet;
import com.github.redouane59.twitter.signature.TwitterCredentials;
import java.util.List;
import java.util.Objects;

public class TwitterUtils{
	private static ITwitterClientV1 client = null;
	
	public static List<Tweet> getUserLastTweets(String userId){
		return getClient().getUserTimeline(userId);
	}
	
	private static ITwitterClientV1 getClient(){
		if(Objects.isNull(client)){
			client = new TwitterClient(TwitterCredentials.builder()
					.accessToken(System.getProperty("TWITTER_TOKEN"))
					.accessTokenSecret(System.getProperty("TWITTER_TOKEN_SECRET"))
					.apiKey(System.getProperty("TWITTER_API"))
					.apiSecretKey(System.getProperty("TWITTER_API_SECRET"))
					.build());
		}
		return client;
	}
	
	public static List<Tweet> getUserLastTweets(String userId, String maxId){
		return getClient().getUserTimeline(userId, 50, maxId);
	}
}
