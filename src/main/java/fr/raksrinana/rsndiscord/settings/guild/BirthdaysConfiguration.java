package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.CompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateSerializer;
import fr.raksrinana.rsndiscord.utils.json.UserConfigurationKeyDeserializer;
import fr.raksrinana.rsndiscord.utils.json.UserConfigurationKeySerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class BirthdaysConfiguration implements CompositeConfiguration{
	@JsonProperty("dates")
	@JsonSerialize(keyUsing = UserConfigurationKeySerializer.class, contentUsing = ISO8601LocalDateSerializer.class)
	@JsonDeserialize(keyUsing = UserConfigurationKeyDeserializer.class, contentUsing = ISO8601LocalDateDeserializer.class)
	@Getter
	@Setter
	private Map<UserConfiguration, LocalDate> dates = new HashMap<>();
	
	public void setDate(User user, LocalDate date){
		this.dates.put(new UserConfiguration(user), date);
	}
	
	public void removeDate(User user){
		this.dates.remove(new UserConfiguration(user));
	}
	
	public Optional<LocalDate> getDate(User user){
		return Optional.ofNullable(dates.get(new UserConfiguration(user)));
	}
}
