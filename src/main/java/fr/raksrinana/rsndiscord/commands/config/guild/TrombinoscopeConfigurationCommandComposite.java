package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.trombinoscope.PicturesChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.trombinoscope.PosterRoleConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.permission.PermissionUtils.ALLOW;

public class TrombinoscopeConfigurationCommandComposite extends CommandComposite{
	public TrombinoscopeConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new PicturesChannelConfigurationCommand(this));
		this.addSubCommand(new PosterRoleConfigurationCommand(this));
	}
	
	@Override
	public @NonNull Permission getPermission(){
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
