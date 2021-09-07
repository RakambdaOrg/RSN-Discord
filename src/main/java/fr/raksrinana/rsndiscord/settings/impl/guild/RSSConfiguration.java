package fr.raksrinana.rsndiscord.settings.impl.guild;

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
	@JsonProperty("lastDate")
	private Map<String, Long> lastPublicationDate = new ConcurrentHashMap<>();
	
	@NotNull
	public Optional<Long> getLastPublicationDate(@NotNull String key){
		return Optional.ofNullable(lastPublicationDate.get(key));
	}
	
	public void setLastPublicationDate(@NotNull String key, long value){
		lastPublicationDate.put(key, value);
	}
	
	@NotNull
	public Optional<ChannelConfiguration> getChannel(){
		return ofNullable(channel);
	}
}
