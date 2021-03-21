package fr.raksrinana.rsndiscord.api.twitter;

import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.dto.tweet.Tweet;
import com.github.redouane59.twitter.signature.TwitterCredentials;
import com.github.scribejava.core.model.Response;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.Settings;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class TwitterApi{
	private static TwitterClient twitteredClient;
	private static Map<String, List<Long>> searches = new HashMap<>();
	private static Future<Response> filteredStream;
	
	public static void registerStreamFilters(){
		removeStreamFilters();
		
		searches = Main.getJda().getGuilds().stream()
				.flatMap(guild -> {
					var conf = Settings.get(guild).getTwitterConfiguration();
					return conf.getSearchChannel()
							.stream()
							.flatMap(channelConfiguration -> conf.getSearches().stream().map(search -> Map.entry(search, channelConfiguration.getChannelId())));
				})
				.collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
		
		searches.keySet().forEach(search -> getTwitteredClient().addFilteredStreamRule(search, Integer.toString(search.hashCode())));
		
		filteredStream = getTwitteredClient().startFilteredStream(TwitterApi::onFilteredStreamTweet);
	}
	
	public static void removeStreamFilters(){
		if(Objects.nonNull(filteredStream)){
			getTwitteredClient().stopFilteredStream(filteredStream);
			filteredStream = null;
		}
		
		getTwitteredClient().retrieveFilteredStreamRules().forEach(rule -> getTwitteredClient().deleteFilteredStreamRule(rule.getValue()));
		searches.clear();
	}
	
	private static void onFilteredStreamTweet(Tweet tweet){
		var tweetUrl = getUrl(tweet);
		
		searches.entrySet().stream()
				.filter(search -> searchMatch(search.getKey(), tweet))
				.flatMap(search -> search.getValue().stream())
				.distinct()
				.map(Main.getJda()::getTextChannelById)
				.filter(Objects::nonNull)
				.forEach(channel -> channel.sendMessage(tweetUrl).submit());
	}
	
	private static boolean searchMatch(String search, Tweet tweet){
		return Arrays.stream(search.split(" ")).allMatch(split -> tweet.getText().contains(split));
	}
	
	@NotNull
	public static List<Tweet> getUserLastTweets(String userId){
		return getUserLastTweets(userId, null);
	}
	
	@NotNull
	public static List<Tweet> getUserLastTweets(String userId, String maxId){
		return getTwitteredClient().getUserTimeline(userId, Integer.MAX_VALUE, null, null, maxId, null);
	}
	
	public static String getUrl(Tweet tweet){
		return "https://twitter.com/i/web/status/%s".formatted(tweet.getId());
	}
	
	@NotNull
	private static TwitterClient getTwitteredClient(){
		if(Objects.isNull(twitteredClient)){
			twitteredClient = new TwitterClient(TwitterCredentials.builder()
					.accessToken(System.getProperty("TWITTER_TOKEN"))
					.accessTokenSecret(System.getProperty("TWITTER_TOKEN_SECRET"))
					.apiKey(System.getProperty("TWITTER_API"))
					.apiSecretKey(System.getProperty("TWITTER_API_SECRET"))
					.build());
		}
		return twitteredClient;
	}
}
