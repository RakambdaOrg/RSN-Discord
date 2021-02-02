package fr.raksrinana.rsndiscord.command.impl.settings.guild.randomkick;

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

public class KickedRoleConfigurationCommand extends RoleConfigurationCommand{
	public KickedRoleConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected void setConfig(@NotNull Guild guild, @NotNull RoleConfiguration value){
		Settings.get(guild).getRandomKick().setKickedRole(value);
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild){
		Settings.get(guild).getRandomKick().setKickedRole(null);
	}
	
	@NotNull
	@Override
	protected Optional<RoleConfiguration> getConfig(@NotNull Guild guild){
		return Settings.get(guild).getRandomKick().getKickedRole();
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Kicked role";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("kickedRole");
	}
}
