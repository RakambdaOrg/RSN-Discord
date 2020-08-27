package fr.raksrinana.rsndiscord.utils.permission;

import lombok.NonNull;

public interface Permission{
	boolean matches(@NonNull String permissionId);
	
	@NonNull
	String getId();
	
	boolean isAllowedByDefault();
}
