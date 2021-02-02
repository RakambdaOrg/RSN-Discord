package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.hermitcraft.StreamingNotificationChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.hermitcraft.VideoNotificationChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class HermitcraftConfigurationCommandComposite extends CommandComposite{
	public HermitcraftConfigurationCommandComposite(Command parent){
		super(parent);
		addSubCommand(new VideoNotificationChannelConfigurationCommand(this));
		addSubCommand(new StreamingNotificationChannelConfigurationCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Hermitcraft";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("hermitcraft");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return "Hermitcraft configurations";
	}
}
