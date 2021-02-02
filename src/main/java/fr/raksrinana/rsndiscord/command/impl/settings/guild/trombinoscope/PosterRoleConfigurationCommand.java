package fr.raksrinana.rsndiscord.command.impl.settings.guild.trombinoscope;

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

public class PosterRoleConfigurationCommand extends RoleConfigurationCommand{
	public PosterRoleConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected void setConfig(@NotNull Guild guild, @NotNull RoleConfiguration value){
		Settings.get(guild).getTrombinoscope().setPosterRole(value);
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild){
		Settings.get(guild).getTrombinoscope().setPosterRole(null);
	}
	
	@NotNull
	@Override
	protected Optional<RoleConfiguration> getConfig(@NotNull Guild guild){
		return Settings.get(guild).getTrombinoscope().getPosterRole();
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Poster role";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("posterRole");
	}
}
