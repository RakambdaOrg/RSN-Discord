package fr.mrcraftcod.gunterdiscord.newSettings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.NickDelayConfig;
import net.dv8tion.jda.api.entities.Guild;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NicknameConfiguration{
	@JsonProperty("changeDelay")
	private long changeDelay = 3600;
	@JsonProperty("lastChange")
	private Map<Long, Long> lastChange = new HashMap<>();
	
	public void mapOldConf(Guild guild){
		this.changeDelay = new NickDelayConfig(guild).getObject().map(i -> i * 60).orElse(3600);
	}
	
	public void setChangeDelay(long changeDelay){
		this.changeDelay = changeDelay;
	}
	
	public long getChangeDelay(){
		return this.changeDelay;
	}
}
