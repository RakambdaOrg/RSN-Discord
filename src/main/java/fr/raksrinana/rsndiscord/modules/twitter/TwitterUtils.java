package fr.raksrinana.rsndiscord.modules.twitter;

import fr.raksrinana.rsndiscord.log.Log;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
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
			var configurationBuilder = new ConfigurationBuilder()
					.setDebugEnabled(true)
					.setOAuthConsumerKey(System.getProperty("TWITTER_API"))
					.setOAuthConsumerSecret(System.getProperty("TWITTER_API_SECRET"))
					.setOAuthAccessToken(System.getProperty("TWITTER_TOKEN"))
					.setOAuthAccessTokenSecret(System.getProperty("TWITTER_TOKEN_SECRET"));
			
			client = new TwitterFactory(configurationBuilder.build()).getInstance();
		}
		return client;
	}
	
	public static List<Status> getUserLastTweets(long userId, long maxId){
		try{
			var page = new Paging();
			page.setSinceId(maxId);
			return getClient().getUserTimeline(userId, page);
		}
		catch(TwitterException e){
			Log.getLogger(null).error("Failed to get tweets of user {}", userId);
		}
		return List.of();
	}
}
