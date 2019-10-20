package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeSerializer;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("FieldMayBeFinal")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NicknameConfiguration{
	@JsonProperty("changeDelay")
	private long changeDelay = 3600;
	@JsonSerialize(contentUsing = LocalDateTimeSerializer.class)
	@JsonDeserialize(contentUsing = LocalDateTimeDeserializer.class)
	@JsonProperty("lastChange")
	private Map<Long, LocalDateTime> lastChange = new HashMap<>();
	
	public Optional<LocalDateTime> getLastChange(@Nonnull final User user){
		return this.getLastChange(user.getIdLong());
	}
	
	private Optional<LocalDateTime> getLastChange(final long userId){
		return Optional.ofNullable(this.lastChange.get(userId));
	}
	
	public void setLastChange(@Nonnull final User user, @Nullable final LocalDateTime date){
		this.lastChange.put(user.getIdLong(), date);
	}
	
	public long getChangeDelay(){
		return this.changeDelay;
	}
	
	public void setChangeDelay(final long changeDelay){
		this.changeDelay = changeDelay;
	}
}
