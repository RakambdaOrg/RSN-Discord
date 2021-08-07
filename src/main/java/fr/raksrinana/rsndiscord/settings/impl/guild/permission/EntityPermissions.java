package fr.raksrinana.rsndiscord.settings.impl.guild.permission;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class EntityPermissions{
	@JsonProperty("granted")
	private final Set<String> granted = new HashSet<>();
	@JsonProperty("denied")
	private final Set<String> denied = new HashSet<>();
	
	public void addFrom(EntityPermissions entityPermissions){
		granted.addAll(entityPermissions.getGranted());
		denied.addAll(entityPermissions.getDenied());
	}
	
	public void grant(@NotNull String permissionId){
		denied.remove(permissionId);
		granted.add(permissionId);
	}
	
	public void deny(@NotNull String permissionId){
		granted.remove(permissionId);
		denied.add(permissionId);
	}
	
	public void reset(@NotNull String permissionId){
		granted.remove(permissionId);
		denied.remove(permissionId);
	}
}
