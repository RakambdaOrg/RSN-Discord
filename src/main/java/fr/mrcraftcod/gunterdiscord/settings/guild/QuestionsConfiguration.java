package fr.mrcraftcod.gunterdiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import javax.annotation.Nullable;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionsConfiguration{
	@JsonProperty("inputChannel")
	private ChannelConfiguration inputChannel;
	@JsonProperty("outputChannel")
	private ChannelConfiguration outputChannel;
	
	public QuestionsConfiguration(){
	}
	
	public Optional<ChannelConfiguration> getInputChannel(){
		return Optional.ofNullable(this.inputChannel);
	}
	
	public void setInputChannel(@Nullable ChannelConfiguration value){
		this.inputChannel = value;
	}
	
	public Optional<ChannelConfiguration> getOutputChannel(){
		return Optional.ofNullable(this.outputChannel);
	}
	
	public void setOutputChannel(@Nullable ChannelConfiguration value){
		this.outputChannel = value;
	}
}
