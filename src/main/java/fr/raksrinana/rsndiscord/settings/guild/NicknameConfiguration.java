package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.utils.json.ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ZonedDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class NicknameConfiguration implements ICompositeConfiguration{
	@JsonSerialize(contentUsing = ZonedDateTimeSerializer.class)
	@JsonDeserialize(contentUsing = ZonedDateTimeDeserializer.class)
	@JsonProperty("lastChange")
	private final Map<Long, ZonedDateTime> lastChange = new HashMap<>();
	@JsonProperty("changeDelay")
	@Setter
	private long changeDelay = 3600;
	
	public Optional<ZonedDateTime> getLastChange(@NonNull final User user){
		return this.getLastChange(user.getIdLong());
	}
	
	private Optional<ZonedDateTime> getLastChange(final long userId){
		return ofNullable(this.lastChange.get(userId));
	}
	
	public void setLastChange(@NonNull final User user, final ZonedDateTime date){
		this.lastChange.put(user.getIdLong(), date);
	}
}
