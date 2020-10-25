package fr.raksrinana.rsndiscord.modules.permission;

import lombok.NonNull;

public interface IPermission{
	boolean matches(@NonNull String permissionId);
	
	@NonNull
	String getId();
	
	boolean isAllowedByDefault();
}
