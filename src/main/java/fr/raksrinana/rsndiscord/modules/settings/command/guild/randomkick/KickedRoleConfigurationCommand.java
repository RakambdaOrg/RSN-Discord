package fr.raksrinana.rsndiscord.modules.settings.command.guild.randomkick;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.modules.settings.command.helpers.RoleConfigurationCommand;
import fr.raksrinana.rsndiscord.modules.settings.types.RoleConfiguration;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

public class KickedRoleConfigurationCommand extends RoleConfigurationCommand{
	public KickedRoleConfigurationCommand(final Command parent){
		super(parent);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected void setConfig(@NonNull final Guild guild, @NonNull final RoleConfiguration value){
		Settings.get(guild).getRandomKick().setKickedRole(value);
	}
	
	@Override
	protected void removeConfig(@NonNull final Guild guild){
		Settings.get(guild).getRandomKick().setKickedRole(null);
	}
	
	@NonNull
	@Override
	protected Optional<RoleConfiguration> getConfig(@NonNull final Guild guild){
		return Settings.get(guild).getRandomKick().getKickedRole();
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Kicked role";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("kickedRole");
	}
}
