package fr.raksrinana.rsndiscord.command.permission;

import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import static fr.raksrinana.rsndiscord.utils.Utilities.isCreator;

public record SimplePermission(boolean allowedByDefault) implements IPermission{
	public static final SimplePermission TRUE_BY_DEFAULT = new SimplePermission(true);
	public static final SimplePermission FALSE_BY_DEFAULT = new SimplePermission(false);
	
	@Override
	public boolean isGuildAllowed(@NotNull String id, @NotNull Member member){
		if(isCreator(member)){
			return true;
		}
		var permissions = Settings.get(member.getGuild())
				.getPermissionsConfiguration()
				.getAggregatedPermissions(member);
		if(isPermissionInList(id, permissions.getGranted())){
			return true;
		}
		if(isPermissionInList(id, permissions.getDenied())){
			return false;
		}
		return allowedByDefault;
	}
	
	@Override
	public boolean isUserAllowed(@NotNull String id, @NotNull User user){
		return true;
	}
	
	private static boolean isPermissionInList(@NotNull String commandId, @NotNull Collection<String> permissions){
		return permissions.stream().anyMatch(permissionId -> SimplePermission.matches(permissionId, commandId));
	}
	
	private static boolean matches(@NotNull String permissionId, @NotNull String commandId){
		if(permissionId.contains("*")){
			var beginning = permissionId.substring(0, permissionId.indexOf("*"));
			if(beginning.endsWith("/")){
				beginning = beginning.substring(0, beginning.length() - 1);
			}
			return commandId.startsWith(beginning);
		}
		return commandId.equals(permissionId);
	}
}
