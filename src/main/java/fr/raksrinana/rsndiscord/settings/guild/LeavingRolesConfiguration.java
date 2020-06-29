package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.CompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.leavingroles.LeaverRoles;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class LeavingRolesConfiguration implements CompositeConfiguration{
	@JsonProperty("leavers")
	private Map<Long, LeaverRoles> leavers = new HashMap<>();
	
	public Optional<LeaverRoles> getLeaver(@NonNull User user){
		return Optional.ofNullable(leavers.get(user.getIdLong()));
	}
	
	public void removeUser(@NonNull User user){
		leavers.remove(user.getIdLong());
	}
	
	public void addLeaver(LeaverRoles leaverRoles){
		leavers.put(leaverRoles.getUser().getUserId(), leaverRoles);
	}
}
