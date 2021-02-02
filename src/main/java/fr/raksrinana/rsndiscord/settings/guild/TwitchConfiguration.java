package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
	
	@NotNull
	public Optional<String> getRandomKickRewardId(){
		return ofNullable(randomKickRewardId);
	}
	
	@NotNull
	public Optional<ChannelConfiguration> getTwitchChannel(){
		return ofNullable(twitchChannel);
	}
}
