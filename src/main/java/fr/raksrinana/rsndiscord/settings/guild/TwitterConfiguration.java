package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class TwitterConfiguration implements ICompositeConfiguration{
	@JsonProperty("usersChannel")
	@JsonAlias("channels")
	@Setter
	private ChannelConfiguration usersChannel;
	@JsonProperty("latestChannel")
	@Setter
	private ChannelConfiguration searchChannel;
	@JsonProperty("userIds")
	@Getter
	@Setter
	private Set<Long> userIds = new HashSet<>();
	@JsonProperty("searches")
	@Getter
	@Setter
	private Set<String> searches = new HashSet<>();
	@JsonProperty("lastUserTweet")
	@JsonAlias("lastTweet")
	@Getter
	@Setter
	private Map<Long, Long> lastUserTweet = new HashMap<>();
	@JsonProperty("lastSearchTweet")
	@Getter
	@Setter
	private Map<String, Long> lastSearchTweet = new HashMap<>();
	
	@NotNull
	public Optional<Long> getLastUserTweet(long userId){
		return ofNullable(lastUserTweet.get(userId));
	}
	
	public void setLastUserTweet(long userId, long tweetId){
		lastUserTweet.put(userId, tweetId);
	}
	
	@NotNull
	public Optional<Long> getLastSearchTweet(@NotNull String search){
		return ofNullable(lastSearchTweet.get(search));
	}
	
	public void setLastSearchTweet(@NotNull String search, long tweetId){
		lastSearchTweet.put(search, tweetId);
	}
	
	@NotNull
	public Optional<ChannelConfiguration> getSearchChannel(){
		return ofNullable(searchChannel);
	}
	
	@NotNull
	public Optional<ChannelConfiguration> getUsersChannel(){
		return ofNullable(usersChannel);
	}
}
