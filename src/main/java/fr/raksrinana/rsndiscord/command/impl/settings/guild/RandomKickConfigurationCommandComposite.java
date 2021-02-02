package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.randomkick.KickRoleProbabilityConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.randomkick.KickableRolesConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.randomkick.KickedRoleConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.randomkick.RandomKickRolesPingRolesConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class RandomKickConfigurationCommandComposite extends CommandComposite{
	public RandomKickConfigurationCommandComposite(Command parent) {
		super(parent);
		addSubCommand(new RandomKickRolesPingRolesConfigurationCommand(this));
		addSubCommand(new KickedRoleConfigurationCommand(this));
		addSubCommand(new KickableRolesConfigurationCommand(this));
		addSubCommand(new KickRoleProbabilityConfigurationCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "RandomKick";
	}
	
	@NotNull
	@Override
	public @NotNull List<String> getCommandStrings(){
		return List.of("randomKick");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return "Random kick configuration";
	}
}
