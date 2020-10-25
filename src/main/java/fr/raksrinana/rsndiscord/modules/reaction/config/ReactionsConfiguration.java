package fr.raksrinana.rsndiscord.modules.reaction.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.modules.settings.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.json.ChannelConfigurationKeyDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ChannelConfigurationKeySerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class ReactionsConfiguration implements ICompositeConfiguration{
	@JsonProperty("autoTodoChannels")
	@Getter
	@Setter
	private Set<ChannelConfiguration> autoTodoChannels = new HashSet<>();
	@JsonProperty("savedForwardingChannels")
	@JsonSerialize(keyUsing = ChannelConfigurationKeySerializer.class)
	@JsonDeserialize(keyUsing = ChannelConfigurationKeyDeserializer.class)
	@Getter
	@Setter
	private Map<ChannelConfiguration, ChannelConfiguration> savedForwarding = new HashMap<>();
}
