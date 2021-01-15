package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.trombinoscope.PicturesChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.trombinoscope.PosterRoleConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class TrombinoscopeConfigurationCommandComposite extends CommandComposite{
	public TrombinoscopeConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new PicturesChannelConfigurationCommand(this));
		this.addSubCommand(new PosterRoleConfigurationCommand(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Trombinoscope";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("trombinoscope", "trombi");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return "Trombinoscope configurations";
	}
}
