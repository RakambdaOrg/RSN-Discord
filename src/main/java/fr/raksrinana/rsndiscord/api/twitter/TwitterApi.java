package fr.raksrinana.rsndiscord.api.twitter;

import com.github.redouane59.twitter.IAPIEventListener;
import com.github.redouane59.twitter.TwitterClient;
import com.github.redouane59.twitter.dto.others.BlockResponse;
import com.github.redouane59.twitter.dto.stream.StreamRules;
import com.github.redouane59.twitter.dto.tweet.Tweet;
import com.github.redouane59.twitter.dto.tweet.TweetV2;
import com.github.redouane59.twitter.signature.TwitterCredentials;
import com.github.scribejava.core.model.Response;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
public class TwitterApi implements IAPIEventListener{
	private static TwitterClient twitteredClient;
	private static Map<String, List<Long>> searches = new HashMap<>();
	private static Map<String, List<Long>> searchesHash = new HashMap<>();
	private static Future<Response> filteredStream;
	
	public static void registerStreamFilters(@NotNull JDA jda){
		removeStreamFilters();
		
		log.info("Registering stream filters");
		searches = jda.getGuilds().stream()
				.flatMap(guild -> {
					var conf = Settings.get(guild).getTwitterConfiguration();
					return conf.getSearchChannel()
							.stream()
							.flatMap(channelConfiguration -> conf.getSearches().stream().map(search -> Map.entry(search, channelConfiguration.getChannelId())));
				})
				.collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
		searchesHash = searches.entrySet().stream().collect(Collectors.toMap(e -> Integer.toString(e.getKey().hashCode()), Map.Entry::getValue));
		
		searches.keySet().forEach(search -> getTwitteredClient().addFilteredStreamRule(search, Integer.toString(search.hashCode())));
		
		filteredStream = getTwitteredClient().startFilteredStream(TwitterApi::onFilteredStreamTweet);
	}
	
	public static void removeStreamFilters(){
		log.info("Removing stream filters");
		if(Objects.nonNull(filteredStream)){
			getTwitteredClient().stopFilteredStream(filteredStream);
			filteredStream = null;
		}
		
		Optional.ofNullable(getTwitteredClient().retrieveFilteredStreamRules())
				.orElse(List.of())
				.forEach(rule -> getTwitteredClient().deleteFilteredStreamRule(rule.getValue()));
		searches.clear();
		searchesHash.clear();
	}
	
	private static void onFilteredStreamTweet(Tweet tweet){
		log.debug("New tweet from filter");
		var tweetUrl = getUrl(tweet);
		
		if(tweet instanceof TweetV2 tweetV2){
			Arrays.stream(tweetV2.getMatchingRules())
					.map(StreamRules.StreamRule::getTag)
					.map(hash -> searchesHash.get(hash))
					.filter(Objects::nonNull)
					.flatMap(List::stream)
					.distinct()
					.map(Main.getJda()::getTextChannelById)
					.filter(Objects::nonNull)
					.forEach(channel -> JDAWrappers.message(channel, tweetUrl).submit());
		}
		else {
			log.error("Tweet isn't a tweet V2");
		}
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
	
	@Override
	public void onStreamError(int httpCode, String error){
		log.error("Filtered stream error, code: {}, error: {}", httpCode, error);
	}
	
	@Override
	public void onTweetStreamed(Tweet tweet){
		onFilteredStreamTweet(tweet);
	}
	
	@Override
	public void onUnknownDataStreamed(String json){
		log.error("Unknown filtered stream data {}", json);
	}
	
	@Override
	public void onStreamEnded(Exception e){
		log.error("Filtered stream ended", e);
		registerStreamFilters(Main.getJda());
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
	
	public static BlockResponse blockUser(String userName){
		var client = getTwitteredClient();
		
		var user = client.getUserFromUserName(userName);
		return client.blockUser(user.getId());
	}
}
