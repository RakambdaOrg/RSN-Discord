package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.trombinoscope.PicturesChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.trombinoscope.PosterRoleConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class TrombinoscopeConfigurationCommandComposite extends CommandComposite{
	public TrombinoscopeConfigurationCommandComposite(Command parent){
		super(parent);
		addSubCommand(new PicturesChannelConfigurationCommand(this));
		addSubCommand(new PosterRoleConfigurationCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Trombinoscope";
	}
	
	@NotNull
	@Override
	public @NotNull List<String> getCommandStrings(){
		return List.of("trombinoscope", "trombi");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return "Trombinoscope configurations";
	}
}
