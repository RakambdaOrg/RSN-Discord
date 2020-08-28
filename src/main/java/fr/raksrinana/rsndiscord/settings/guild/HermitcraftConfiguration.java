package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.CompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class HermitcraftConfiguration implements CompositeConfiguration{
	@JsonProperty("videoNotificationChannel")
	@Setter
	private ChannelConfiguration videoNotificationChannel;
	@JsonProperty("streamingNotificationChannel")
	@Setter
	private ChannelConfiguration streamingNotificationChannel;
	
	public Optional<ChannelConfiguration> getStreamingNotificationChannel(){
		return Optional.ofNullable(streamingNotificationChannel);
	}
	
	public Optional<ChannelConfiguration> getVideoNotificationChannel(){
		return Optional.ofNullable(videoNotificationChannel);
	}
}
