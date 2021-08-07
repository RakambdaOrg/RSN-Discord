package fr.raksrinana.rsndiscord.settings.impl.guild.reaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.api.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.json.converter.ChannelConfigurationKeyDeserializer;
import fr.raksrinana.rsndiscord.utils.json.converter.ChannelConfigurationKeySerializer;
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
