package fr.raksrinana.rsndiscord.modules.permission;

import fr.raksrinana.rsndiscord.modules.settings.Settings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Member;
import java.util.Collection;
import static fr.raksrinana.rsndiscord.utils.Utilities.isCreator;

@Data
@AllArgsConstructor
public class SimplePermission implements IPermission{
	@NonNull
	private String id;
	private boolean allowedByDefault;
	
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
	
	@Override
	public boolean isAllowed(@NonNull Member member){
		if(isCreator(member)){
			return true;
		}
		var permissions = Settings.get(member.getGuild())
				.getPermissionsConfiguration()
				.getAggregatedPermissions(member);
		if(isPermissionInList(this, permissions.getDenied())){
			return false;
		}
		return isAllowedByDefault() || isPermissionInList(this, permissions.getGranted());
	}
	
	private static boolean isPermissionInList(SimplePermission permission, Collection<String> permissions){
		return permissions.stream().anyMatch(permission::matches);
	}
}
