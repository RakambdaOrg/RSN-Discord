package fr.raksrinana.rsndiscord.utils.permission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class SimplePermission implements Permission{
	@NonNull
	private String id;
	private boolean allowedByDefault;
	
	@Override
	public boolean matches(@NonNull String permissionId){
		if(getId().contains("*")){
			var beginning = getId().substring(0, getId().indexOf("*"));
			return permissionId.startsWith(beginning);
		}
		return getId().equals(permissionId);
	}
}
