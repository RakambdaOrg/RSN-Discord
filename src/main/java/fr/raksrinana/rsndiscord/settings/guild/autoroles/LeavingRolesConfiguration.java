package fr.raksrinana.rsndiscord.settings.guild.autoroles;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.ICompositeConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class LeavingRolesConfiguration implements ICompositeConfiguration{
	@JsonProperty("leavers")
	private final Map<Long, LeaverRoles> leavers = new HashMap<>();
	
	public Optional<LeaverRoles> getLeaver(@NonNull User user){
		return ofNullable(leavers.get(user.getIdLong()));
	}
	
	public void removeUser(@NonNull User user){
		leavers.remove(user.getIdLong());
	}
	
	public void addLeaver(LeaverRoles leaverRoles){
		leavers.put(leaverRoles.getUser().getUserId(), leaverRoles);
	}
}
