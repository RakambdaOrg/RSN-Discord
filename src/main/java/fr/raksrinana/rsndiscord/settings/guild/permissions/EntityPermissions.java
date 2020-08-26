package fr.raksrinana.rsndiscord.settings.guild.permissions;

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
	private Set<String> granted = new HashSet<>();
	@JsonProperty("denied")
	private Set<String> denied = new HashSet<>();
	
	public void addFrom(EntityPermissions entityPermissions){
		this.granted.addAll(entityPermissions.getGranted());
		this.denied.addAll(entityPermissions.getDenied());
	}
}
