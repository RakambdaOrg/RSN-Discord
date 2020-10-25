package fr.raksrinana.rsndiscord.modules.permission;

import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import java.util.Collection;

public class PermissionUtils{
	public static final IPermission ALLOW = new SimplePermission("ALLOW", true);
	
	public static boolean isUserAllowed(@NonNull Member member, @NonNull IPermission permission){
		if(Utilities.isCreator(member)){
			return true;
		}
		var permissions = Settings.get(member.getGuild())
				.getPermissionsConfiguration()
				.getAggregatedPermissions(member);
		if(isPermissionInList(permission, permissions.getDenied())){
			return false;
		}
		return permission.isAllowedByDefault() || isPermissionInList(permission, permissions.getGranted());
	}
	
	private static boolean isPermissionInList(IPermission permission, Collection<String> permissions){
		return permissions.stream().anyMatch(permission::matches);
	}
}
