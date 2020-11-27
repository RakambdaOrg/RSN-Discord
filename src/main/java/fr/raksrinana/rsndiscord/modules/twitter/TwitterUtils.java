package fr.raksrinana.rsndiscord.modules.twitter;

import fr.raksrinana.rsndiscord.log.Log;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import java.util.List;
import java.util.Objects;
import static twitter4j.Query.RECENT;

public class TwitterUtils{
	private static Twitter client = null;
	
	public static List<Status> getUserLastTweets(long userId){
		return getUserLastTweets(userId, -1);
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
			var page = new Paging().count(20);
			
			if(maxId > 0){
				page.setSinceId(maxId);
			}
			
			return getClient().getUserTimeline(userId, page);
		}
		catch(TwitterException e){
			Log.getLogger(null).error("Failed to get tweets of user {}", userId);
		}
		return List.of();
	}
	
	public static List<Status> searchLastTweets(String search){
		return searchLastTweets(search, -1);
	}
	
	public static List<Status> searchLastTweets(String search, long maxId){
		try{
			return getClient()
					.search(new Query(search)
							.count(20)
							.sinceId(maxId)
							.resultType(RECENT))
					.getTweets();
		}
		catch(TwitterException e){
			Log.getLogger(null).error("Failed to get latest tweets for search {}", search);
		}
		return List.of();
	}
}
