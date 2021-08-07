package fr.raksrinana.rsndiscord.settings.impl.guild.permission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
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
	
	@NotNull
	public EntityPermissions getAggregatedPermissions(@NotNull Member member){
		return Stream.concat(
				getUserPermissions(member.getUser()).stream(),
				member.getRoles().stream().map(this::getRolePermissions).flatMap(Optional::stream)
		).collect(EntityPermissions::new, EntityPermissions::addFrom, EntityPermissions::addFrom);
	}
	
	@NotNull
	public Optional<EntityPermissions> getUserPermissions(@NotNull User user){
		return ofNullable(getUsersPermissions().get(user.getIdLong()));
	}
	
	@NotNull
	public Optional<EntityPermissions> getRolePermissions(@NotNull Role role){
		return ofNullable(getRolesPermissions().get(role.getIdLong()));
	}
	
	public void grant(@NotNull User user, @NotNull String permissionId){
		usersPermissions.computeIfAbsent(user.getIdLong(), key -> new EntityPermissions())
				.grant(permissionId);
	}
	
	public void grant(@NotNull Role role, @NotNull String permissionId){
		rolesPermissions.computeIfAbsent(role.getIdLong(), key -> new EntityPermissions())
				.grant(permissionId);
	}
	
	public void deny(@NotNull User user, @NotNull String permissionId){
		ofNullable(usersPermissions.get(user.getIdLong()))
				.ifPresent(entityPermissions -> entityPermissions.deny(permissionId));
	}
	
	public void deny(@NotNull Role role, @NotNull String permissionId){
		ofNullable(rolesPermissions.get(role.getIdLong()))
				.ifPresent(entityPermissions -> entityPermissions.deny(permissionId));
	}
	
	public void reset(@NotNull User user, @NotNull String permissionId){
		ofNullable(usersPermissions.get(user.getIdLong()))
				.ifPresent(entityPermissions -> entityPermissions.reset(permissionId));
	}
	
	public void reset(@NotNull Role role, @NotNull String permissionId){
		ofNullable(rolesPermissions.get(role.getIdLong()))
				.ifPresent(entityPermissions -> entityPermissions.reset(permissionId));
	}
}
