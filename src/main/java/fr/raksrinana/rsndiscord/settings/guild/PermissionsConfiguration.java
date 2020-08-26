package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.guild.permissions.EntityPermissions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class PermissionsConfiguration{
	@JsonProperty("userPermissions")
	@Getter
	@Setter
	private Map<Long, EntityPermissions> usersPermissions = new HashMap<>();
	@JsonProperty("rolesPermissions")
	@Getter
	@Setter
	private Map<Long, EntityPermissions> rolesPermissions = new HashMap<>();
	
	public EntityPermissions getAggregatedPermissions(@NonNull Member member){
		return Stream.concat(
				getUserPermissions(member.getUser()).stream(),
				member.getRoles().stream().map(this::getRolePermissions).flatMap(Optional::stream)
		).collect(EntityPermissions::new, EntityPermissions::addFrom, EntityPermissions::addFrom);
	}
	
	public Optional<EntityPermissions> getUserPermissions(@NonNull User user){
		return Optional.of(getUsersPermissions().get(user.getIdLong()));
	}
	
	public Optional<EntityPermissions> getRolePermissions(@NonNull Role role){
		return Optional.of(getRolesPermissions().get(role.getIdLong()));
	}
}
