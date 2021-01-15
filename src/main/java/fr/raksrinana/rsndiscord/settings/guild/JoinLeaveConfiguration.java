package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class JoinLeaveConfiguration implements ICompositeConfiguration{
	@JsonProperty("channel")
	@Setter
	private ChannelConfiguration channel;
	@JsonProperty("joinImages")
	@Getter
	@Setter
	private Set<String> joinImages = new HashSet<>();
	@JsonProperty("leaveImages")
	@Getter
	@Setter
	private Set<String> leaveImages = new HashSet<>();
	
	public Optional<ChannelConfiguration> getChannel(){
		return Optional.ofNullable(channel);
	}
}
