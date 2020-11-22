package fr.raksrinana.rsndiscord.modules.twitter.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.modules.settings.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.*;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class TwitterConfiguration implements ICompositeConfiguration{
	@JsonProperty("channels")
	@Setter
	private ChannelConfiguration channel;
	@JsonProperty("userIds")
	@Getter
	@Setter
	private Set<Long> userIds = new HashSet<>();
	@JsonProperty("lastTweet")
	@Getter
	@Setter
	private Map<Long, Long> lastTweet = new HashMap<>();
	
	public Optional<Long> getLastTweet(long userId){
		return ofNullable(lastTweet.get(userId));
	}
	
	public void setLastTweet(long userId, long tweetId){
		lastTweet.put(userId, tweetId);
	}
	
	public Optional<ChannelConfiguration> getChannel(){
		return ofNullable(channel);
	}
}
