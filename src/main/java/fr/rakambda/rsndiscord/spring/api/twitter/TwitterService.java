package fr.rakambda.rsndiscord.spring.api.twitter;

import fr.rakambda.rsndiscord.spring.api.exceptions.RequestFailedException;
import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.endpoints.AdditionalParameters;
import io.github.redouane59.twitter.dto.tweet.Tweet;
import io.github.redouane59.twitter.dto.tweet.TweetList;
import io.github.redouane59.twitter.dto.user.User;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
@Slf4j
public class TwitterService{
	private final TwitterClient client;
	
	public TwitterService(TwitterClient client){
		this.client = client;
	}
	
	@NotNull
	public User getUserIdFromUsername(@NotNull String username) throws RequestFailedException{
		try{
			return client.getUserFromUserName(username);
		}
		catch(Exception e){
			throw new RequestFailedException("Failed to get user id for " + username, e);
		}
	}
	
	@NotNull
	public TweetList getUserLastTweets(@NotNull String userId, @Nullable String maxId) throws RequestFailedException{
		try{
			log.info("Getting last user {} tweets", userId);
			
			var additionalParams = AdditionalParameters.builder()
					.maxResults(100)
					.sinceId(maxId)
					.recursiveCall(true)
					.build();
			return client.getUserTimeline(userId, additionalParams);
		}
		catch(Exception e){
			throw new RequestFailedException("Failed to get user tweets for " + userId, e);
		}
	}
	
	@NotNull
	public String getUrl(@NotNull Tweet tweet){
		if(Objects.nonNull(tweet.getUser())){
			return "https://twitter.com/%s/status/%s".formatted(tweet.getUser().getName(), tweet.getId());
		}
		return "https://twitter.com/i/web/status/%s".formatted(tweet.getId());
	}
}
