package fr.raksrinana.rsndiscord.settings.guild.participation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.IAtomicConfiguration;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
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
public class ChatParticipation implements IAtomicConfiguration{
	@JsonProperty("day")
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@JsonSerialize(using = ISO8601LocalDateSerializer.class)
	private LocalDate day;
	@JsonProperty("userCounts")
	private final Map<Long, Long> userCounts = new HashMap<>();
	
	public ChatParticipation(@NotNull LocalDate day){
		this.day = day;
	}
	
	public long incrementUser(@NotNull User user){
		return userCounts.compute(user.getIdLong(), (key, value) -> {
			if(isNull(value)){
				value = 0L;
			}
			return value + 1;
		});
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return between(day, LocalDate.now(UTC)).getDays() > 15;
	}
}
