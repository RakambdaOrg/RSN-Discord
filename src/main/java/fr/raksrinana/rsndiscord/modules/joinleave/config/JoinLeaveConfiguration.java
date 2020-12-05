package fr.raksrinana.rsndiscord.modules.joinleave.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.modules.settings.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class JoinLeaveConfiguration implements ICompositeConfiguration{
	@JsonProperty("channel")
	@Setter
	private ChannelConfiguration channel;
	
	public Optional<ChannelConfiguration> getChannel(){
		return Optional.ofNullable(channel);
	}
}
