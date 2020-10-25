package fr.raksrinana.rsndiscord.modules.permission.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
		this.granted.addAll(entityPermissions.getGranted());
		this.denied.addAll(entityPermissions.getDenied());
	}
	
	public void grant(String permissionId){
		denied.remove(permissionId);
		granted.add(permissionId);
	}
	
	public void deny(String permissionId){
		granted.remove(permissionId);
		denied.add(permissionId);
	}
	
	public void reset(String permissionId){
		granted.remove(permissionId);
		denied.remove(permissionId);
	}
}
