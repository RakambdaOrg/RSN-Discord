package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.hermitcraft.StreamingNotificationChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.hermitcraft.VideoNotificationChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.utils.permission.Permission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.utils.permission.PermissionUtils.ALLOW;

public class HermitcraftConfigurationCommandComposite extends CommandComposite{
	public HermitcraftConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new VideoNotificationChannelConfigurationCommand(this));
		this.addSubCommand(new StreamingNotificationChannelConfigurationCommand(this));
	}
	
	@Override
	public @NonNull Permission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Hermitcraft";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("hermitcraft");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return "Hermitcraft configurations";
	}
}
