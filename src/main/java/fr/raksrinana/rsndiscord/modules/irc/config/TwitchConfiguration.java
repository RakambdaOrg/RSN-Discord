package fr.raksrinana.rsndiscord.modules.irc.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.modules.settings.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@Slf4j
public class TwitchConfiguration implements ICompositeConfiguration{
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
	@JsonProperty("randomKickRewardId")
	@Setter
	private String randomKickRewardId;
	
	public Optional<String> getRandomKickRewardId(){
		return ofNullable(randomKickRewardId);
	}
	
	@NonNull
	public Optional<ChannelConfiguration> getTwitchChannel(){
		return ofNullable(this.twitchChannel);
	}
}
