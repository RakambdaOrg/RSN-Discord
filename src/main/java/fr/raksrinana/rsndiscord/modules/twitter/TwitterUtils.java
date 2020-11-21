package fr.raksrinana.rsndiscord.modules.twitter;

import fr.raksrinana.rsndiscord.log.Log;
import twitter4j.*;
import java.util.List;
import java.util.Objects;

public class TwitterUtils{
	private static Twitter client = null;
	
	public static List<Status> getUserLastTweets(long userId){
		try{
			return getClient().getUserTimeline(userId);
		}
		catch(TwitterException e){
			Log.getLogger(null).error("Failed to get tweets of user {}", userId);
		}
		return List.of();
	}
	
	private static Twitter getClient(){
		if(Objects.isNull(client)){
			client = TwitterFactory.getSingleton();
			client.setOAuthConsumer(System.getProperty("TWITTER_API"), System.getProperty("TWITTER_API_SECRET"));
			// client = new TwitterClient(TwitterCredentials.builder()
			// 		.accessToken(System.getProperty("TWITTER_TOKEN"))
			// 		.accessTokenSecret(System.getProperty("TWITTER_TOKEN_SECRET"))
			// 		.apiKey(System.getProperty("TWITTER_API"))
			// 		.apiSecretKey(System.getProperty("TWITTER_API_SECRET"))
			// 		.build());
		}
		return client;
	}
	
	public static List<Status> getUserLastTweets(long userId, long maxId){
		try{
			var page = new Paging();
			page.setMaxId(maxId);
			return getClient().getUserTimeline(userId, page);
		}
		catch(TwitterException e){
			Log.getLogger(null).error("Failed to get tweets of user {}", userId);
		}
		return List.of();
	}
}
