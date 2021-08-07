package fr.raksrinana.rsndiscord.settings.impl.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.api.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.json.converter.ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.converter.ZonedDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
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
public class EventConfiguration implements ICompositeConfiguration{
	@JsonProperty("winnerRole")
	@Setter
	private RoleConfiguration winnerRole;
	@JsonProperty("obtainedTime")
	@Setter
	@JsonDeserialize(contentUsing = ZonedDateTimeDeserializer.class)
	@JsonSerialize(contentUsing = ZonedDateTimeSerializer.class)
	private Map<Long, ZonedDateTime> looseRoleTime = new HashMap<>();
	
	public void setLooseRoleTime(@NotNull Member member, @NotNull ZonedDateTime date){
		looseRoleTime.put(member.getIdLong(), date);
	}
	
	public boolean isRoleTimeOver(@NotNull Member member){
		return Optional.ofNullable(looseRoleTime.getOrDefault(member.getIdLong(), null))
				.map(date -> date.isBefore(ZonedDateTime.now()))
				.orElse(true);
	}
	
	@NotNull
	public Optional<RoleConfiguration> getWinnerRole(){
		return ofNullable(winnerRole);
	}
}
