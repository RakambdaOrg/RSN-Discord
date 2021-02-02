package fr.raksrinana.rsndiscord.permission;

import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import static fr.raksrinana.rsndiscord.utils.Utilities.isCreator;

@Data
@AllArgsConstructor
public class SimplePermission implements IPermission{
	@NotNull
	private String id;
	private boolean allowedByDefault;
	
	@Override
	public boolean isAllowed(@NotNull Member member){
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
	
	private static boolean isPermissionInList(@NotNull SimplePermission permission, @NotNull Collection<String> permissions){
		return permissions.stream().anyMatch(permission::matches);
	}
	
	public boolean matches(@NotNull String permissionId){
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
