package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.CompositeConfiguration;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class NicknameConfiguration implements CompositeConfiguration{
	@JsonProperty("changeDelay")
	@Setter
	private long changeDelay = 3600;
	@JsonSerialize(contentUsing = LocalDateTimeSerializer.class)
	@JsonDeserialize(contentUsing = LocalDateTimeDeserializer.class)
	@JsonProperty("lastChange")
	private Map<Long, LocalDateTime> lastChange = new HashMap<>();
	
	public Optional<LocalDateTime> getLastChange(@NonNull final User user){
		return this.getLastChange(user.getIdLong());
	}
	
	private Optional<LocalDateTime> getLastChange(final long userId){
		return Optional.ofNullable(this.lastChange.get(userId));
	}
	
	public void setLastChange(@NonNull final User user, final LocalDateTime date){
		this.lastChange.put(user.getIdLong(), date);
	}
}
