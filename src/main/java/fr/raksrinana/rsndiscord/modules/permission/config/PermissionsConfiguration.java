package fr.raksrinana.rsndiscord.modules.permission.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class PermissionsConfiguration{
	@JsonProperty("userPermissions")
	@Getter
	@Setter
	private Map<Long, EntityPermissions> usersPermissions = new ConcurrentHashMap<>();
	@JsonProperty("rolesPermissions")
	@Getter
	@Setter
	private Map<Long, EntityPermissions> rolesPermissions = new ConcurrentHashMap<>();
	
	public EntityPermissions getAggregatedPermissions(@NonNull Member member){
		return Stream.concat(
				getUserPermissions(member.getUser()).stream(),
				member.getRoles().stream().map(this::getRolePermissions).flatMap(Optional::stream)
		).collect(EntityPermissions::new, EntityPermissions::addFrom, EntityPermissions::addFrom);
	}
	
	public Optional<EntityPermissions> getUserPermissions(@NonNull User user){
		return ofNullable(getUsersPermissions().get(user.getIdLong()));
	}
	
	public Optional<EntityPermissions> getRolePermissions(@NonNull Role role){
		return ofNullable(getRolesPermissions().get(role.getIdLong()));
	}
	
	public void grant(User user, String permissionId){
		usersPermissions.computeIfAbsent(user.getIdLong(), key -> new EntityPermissions())
				.grant(permissionId);
	}
	
	public void grant(Role role, String permissionId){
		rolesPermissions.computeIfAbsent(role.getIdLong(), key -> new EntityPermissions())
				.grant(permissionId);
	}
	
	public void deny(User user, String permissionId){
		ofNullable(usersPermissions.get(user.getIdLong()))
				.ifPresent(entityPermissions -> entityPermissions.deny(permissionId));
	}
	
	public void deny(Role role, String permissionId){
		ofNullable(rolesPermissions.get(role.getIdLong()))
				.ifPresent(entityPermissions -> entityPermissions.deny(permissionId));
	}
	
	public void reset(User user, String permissionId){
		ofNullable(usersPermissions.get(user.getIdLong()))
				.ifPresent(entityPermissions -> entityPermissions.reset(permissionId));
	}
	
	public void reset(Role role, String permissionId){
		ofNullable(rolesPermissions.get(role.getIdLong()))
				.ifPresent(entityPermissions -> entityPermissions.reset(permissionId));
	}
}
