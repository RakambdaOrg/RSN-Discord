package fr.raksrinana.rsndiscord.modules.participation.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.modules.settings.IAtomicConfiguration;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import static java.time.Period.between;
import static java.time.ZoneOffset.UTC;
import static java.util.Objects.isNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class VoiceParticipation implements IAtomicConfiguration{
	@JsonProperty("day")
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@JsonSerialize(using = ISO8601LocalDateSerializer.class)
	private LocalDate day;
	@JsonProperty("userCounts")
	private final Map<Long, Long> userCounts = new HashMap<>();
	
	public VoiceParticipation(LocalDate day){
		this.day = day;
	}
	
	public long incrementUser(User user, long amount){
		return this.userCounts.compute(user.getIdLong(), (key, value) -> {
			if(isNull(value)){
				value = 0L;
			}
			return value + amount;
		});
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return between(day, LocalDate.now(UTC)).getDays() > 15;
	}
}
