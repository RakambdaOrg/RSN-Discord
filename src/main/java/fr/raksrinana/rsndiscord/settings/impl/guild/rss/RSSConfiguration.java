package fr.raksrinana.rsndiscord.settings.impl.guild.rss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.api.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.json.converter.URLDeserializer;
import fr.raksrinana.rsndiscord.utils.json.converter.URLSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class RSSConfiguration implements ICompositeConfiguration{
	@JsonProperty("channel")
	@Setter
	private ChannelConfiguration channel;
	@JsonProperty("feeds")
	@Getter
	@JsonDeserialize(contentUsing = URLDeserializer.class)
	@JsonSerialize(contentUsing = URLSerializer.class)
	private Set<URL> feeds = new HashSet<>();
	@JsonProperty("feedInfo")
	@Getter
	private Map<String, FeedInfo> feedInfo = new ConcurrentHashMap<>();
	
	@NotNull
	public FeedInfo getFeedInfo(@NotNull String key){
		return feedInfo.computeIfAbsent(key, k -> new FeedInfo());
	}
	
	@NotNull
	public Optional<ChannelConfiguration> getChannel(){
		return ofNullable(channel);
	}
}
