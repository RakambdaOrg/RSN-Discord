package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.trakt.MediaChangeChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.trakt.ThaChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.trakt.ThaUserConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class TraktConfigurationCommandComposite extends CommandComposite{
	public TraktConfigurationCommandComposite(Command parent){
		super(parent);
		addSubCommand(new ThaChannelConfigurationCommand(this));
		addSubCommand(new ThaUserConfigurationCommand(this));
		addSubCommand(new MediaChangeChannelConfigurationCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Trakt";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("trakt");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return "Trakt configurations";
	}
}
