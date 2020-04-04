package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.CompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("FieldMayBeFinal")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@Slf4j
public class TwitchConfiguration implements CompositeConfiguration{
	@JsonProperty("twitchAutoConnectUsers")
	@Getter
	@Setter
	private Set<String> twitchAutoConnectUsers = new HashSet<>();
	@JsonProperty("twitchChannel")
	@Setter
	private ChannelConfiguration twitchChannel;
	@JsonProperty("ircForward")
	@Getter
	@Setter
	private boolean ircForward = false;
	
	@NonNull
	public Optional<ChannelConfiguration> getTwitchChannel(){
		return Optional.ofNullable(this.twitchChannel);
	}
}
