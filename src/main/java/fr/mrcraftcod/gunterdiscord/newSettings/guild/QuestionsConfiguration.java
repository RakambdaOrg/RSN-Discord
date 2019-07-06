package fr.mrcraftcod.gunterdiscord.newSettings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.newSettings.types.ChannelConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.QuestionsChannelConfig;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.QuestionsFinalChannelConfig;
import net.dv8tion.jda.api.entities.Guild;
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
	
	public void mapOldConf(Guild guild){
		this.inputChannel = new QuestionsChannelConfig(guild).getObject().map(ChannelConfiguration::new).orElse(null);
		this.outputChannel = new QuestionsFinalChannelConfig(guild).getObject().map(ChannelConfiguration::new).orElse(null);
	}
	
	public void setInputChannel(@Nullable ChannelConfiguration value){
		this.inputChannel = value;
	}
	
	public void setOutputChannel(@Nullable ChannelConfiguration value){
		this.outputChannel = value;
	}
	
	public Optional<ChannelConfiguration> getInputChannel(){
		return Optional.ofNullable(this.inputChannel);
	}
	
	public Optional<ChannelConfiguration> getOutputChannel(){
		return Optional.ofNullable(this.outputChannel);
	}
}
