package fr.mrcraftcod.gunterdiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.mrcraftcod.gunterdiscord.utils.json.LocalDateTimeDeserializer;
import fr.mrcraftcod.gunterdiscord.utils.json.LocalDateTimeSerializer;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NicknameConfiguration{
	@JsonProperty("changeDelay")
	private long changeDelay = 3600;
	@JsonSerialize(contentUsing = LocalDateTimeSerializer.class)
	@JsonDeserialize(contentUsing = LocalDateTimeDeserializer.class)
	@JsonProperty("lastChange")
	private Map<Long, LocalDateTime> lastChange = new HashMap<>();
	
	public Optional<LocalDateTime> getLastChange(@Nonnull User user){
		return this.getLastChange(user.getIdLong());
	}
	
	private Optional<LocalDateTime> getLastChange(long userId){
		return Optional.ofNullable(this.lastChange.get(userId));
	}
	
	public void setLastChange(@Nonnull User user, @Nullable LocalDateTime date){
		this.lastChange.put(user.getIdLong(), date);
	}
	
	public long getChangeDelay(){
		return this.changeDelay;
	}
	
	public void setChangeDelay(long changeDelay){
		this.changeDelay = changeDelay;
	}
}
