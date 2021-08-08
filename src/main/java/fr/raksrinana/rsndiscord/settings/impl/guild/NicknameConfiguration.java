package fr.raksrinana.rsndiscord.settings.impl.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.api.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.utils.json.converter.ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.converter.ZonedDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
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
	private Long changeDelay;
	
	@NotNull
	public Optional<Long> getChangeDelay(){
		return Optional.ofNullable(changeDelay);
	}
	
	@NotNull
	public Optional<ZonedDateTime> getLastChange(@NotNull User user){
		return getLastChange(user.getIdLong());
	}
	
	@NotNull
	private Optional<ZonedDateTime> getLastChange(long userId){
		return ofNullable(lastChange.get(userId));
	}
	
	public void setLastChange(@NotNull User user, @NotNull ZonedDateTime date){
		lastChange.put(user.getIdLong(), date);
	}
}
