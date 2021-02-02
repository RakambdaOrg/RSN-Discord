package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.RoleConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class EventWinnerRoleConfigurationCommand extends RoleConfigurationCommand{
	public EventWinnerRoleConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	protected void setConfig(@NotNull Guild guild, @NotNull RoleConfiguration value){
		Settings.get(guild).setEventWinnerRole(value);
	}
	
	@Override
	protected void removeConfig(@NotNull @NotNull Guild guild){
		Settings.get(guild).setEventWinnerRole(null);
	}
	
	@NotNull
	@Override
	protected Optional<RoleConfiguration> getConfig(@NotNull Guild guild){
		return Settings.get(guild).getEventWinnerRole();
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Event winner role";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("eventWinnerRole");
	}
}
