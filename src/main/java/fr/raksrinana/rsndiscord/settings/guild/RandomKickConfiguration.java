package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.CompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class RandomKickConfiguration implements CompositeConfiguration{
	@JsonProperty("randomKickRolesPing")
	@Getter
	@Setter
	private Set<RoleConfiguration> randomKickRolesPing = new HashSet<>();
	@JsonProperty("kickedRole")
	@Setter
	private RoleConfiguration kickedRole;
	
	public Optional<RoleConfiguration> getKickedRole(){
		return Optional.ofNullable(kickedRole);
	}
}
