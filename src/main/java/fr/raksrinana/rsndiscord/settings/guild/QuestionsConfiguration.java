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
public class QuestionsConfiguration implements CompositeConfiguration{
	@JsonProperty("inputChannel")
	@Setter
	private ChannelConfiguration inputChannel;
	@JsonProperty("outputChannel")
	@Setter
	private ChannelConfiguration outputChannel;
	
	public Optional<ChannelConfiguration> getInputChannel(){
		return Optional.ofNullable(this.inputChannel);
	}
	
	public Optional<ChannelConfiguration> getOutputChannel(){
		return Optional.ofNullable(this.outputChannel);
	}
}
