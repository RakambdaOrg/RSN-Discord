package fr.raksrinana.rsndiscord.command2.permission;

import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import static fr.raksrinana.rsndiscord.utils.Utilities.isCreator;

public record SimplePermission(boolean allowedByDefault) implements IPermission{
	public static final SimplePermission TRUE_BY_DEFAULT = new SimplePermission(true);
	public static final SimplePermission FALSE_BY_DEFAULT = new SimplePermission(false);
	
	@Override
	public boolean isAllowed(@NotNull String id, @NotNull Member member){
		if(isCreator(member)){
			return true;
		}
		var permissions = Settings.get(member.getGuild())
				.getPermissionsConfiguration()
				.getAggregatedPermissions(member);
		if(isPermissionInList(id, permissions.getDenied())){
			return false;
		}
		return allowedByDefault || isPermissionInList(id, permissions.getGranted());
	}
	
	private static boolean isPermissionInList(@NotNull String commandId, @NotNull Collection<String> permissions){
		return permissions.stream().anyMatch(permissionId -> SimplePermission.matches(permissionId, commandId));
	}
	
	private static boolean matches(@NotNull String permissionId, @NotNull String commandId){
		if(permissionId.contains("*")){
			var beginning = permissionId.substring(0, permissionId.indexOf("*"));
			return commandId.startsWith(beginning);
		}
		return commandId.equals(permissionId);
	}
}
