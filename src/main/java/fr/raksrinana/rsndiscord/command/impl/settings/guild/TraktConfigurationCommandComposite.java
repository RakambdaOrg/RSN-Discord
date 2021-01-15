package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.trakt.MediaChangeChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.trakt.ThaChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.trakt.ThaUserConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class TraktConfigurationCommandComposite extends CommandComposite{
	public TraktConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new ThaChannelConfigurationCommand(this));
		this.addSubCommand(new ThaUserConfigurationCommand(this));
		this.addSubCommand(new MediaChangeChannelConfigurationCommand(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Trakt";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("trakt");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return "Trakt configurations";
	}
}
