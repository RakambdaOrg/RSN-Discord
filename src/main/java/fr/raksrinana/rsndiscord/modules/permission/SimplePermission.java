package fr.raksrinana.rsndiscord.modules.permission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class SimplePermission implements IPermission{
	@NonNull
	private String id;
	private boolean allowedByDefault;
	
	@Override
	public boolean matches(@NonNull String permissionId){
		if(permissionId.contains("*")){
			var beginning = permissionId.substring(0, permissionId.indexOf("*"));
			if(beginning.endsWith(".")){
				beginning = beginning.substring(0, beginning.length() - 1);
			}
			return getId().startsWith(beginning);
		}
		return getId().equals(permissionId);
	}
}
