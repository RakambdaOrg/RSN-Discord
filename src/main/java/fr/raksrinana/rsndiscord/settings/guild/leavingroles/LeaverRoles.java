package fr.raksrinana.rsndiscord.settings.guild.leavingroles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.AtomicConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.utils.json.ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ZonedDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class LeaverRoles implements AtomicConfiguration{
	@JsonProperty("user")
	private UserConfiguration user;
	@JsonProperty("roles")
	private Set<RoleConfiguration> roles = new HashSet<>();
	@JsonProperty("leaveDate")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	private ZonedDateTime leaveDate;
	
	public LeaverRoles(User user, List<Role> roles){
		this.user = new UserConfiguration(user);
		this.roles = roles.stream()
				.map(RoleConfiguration::new)
				.collect(Collectors.toSet());
		this.leaveDate = ZonedDateTime.now();
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return leaveDate.plus(1, ChronoUnit.MONTHS).isBefore(ZonedDateTime.now());
	}
}