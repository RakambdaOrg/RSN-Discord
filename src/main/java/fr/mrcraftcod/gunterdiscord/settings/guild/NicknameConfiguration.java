package fr.mrcraftcod.gunterdiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NicknameConfiguration{
	@JsonProperty("changeDelay")
	private long changeDelay = 3600;
	@JsonProperty("lastChange")
	private Map<Long, Long> lastChange = new HashMap<>();
	
	public Optional<Long> getLastChange(@Nonnull User user){
		return this.getLastChange(user.getIdLong());
	}
	
	private Optional<Long> getLastChange(long userId){
		return Optional.ofNullable(this.lastChange.get(userId));
	}
	
	public void setLastChange(@Nonnull User user, @Nonnull Date date){
		this.lastChange.put(user.getIdLong(), date.getTime());
	}
	
	public long getChangeDelay(){
		return this.changeDelay;
	}
	
	public void setChangeDelay(long changeDelay){
		this.changeDelay = changeDelay;
	}
}
