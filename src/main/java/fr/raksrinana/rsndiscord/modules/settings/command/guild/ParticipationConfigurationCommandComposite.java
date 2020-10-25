package fr.raksrinana.rsndiscord.modules.settings.command.guild;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.participation.IgnoredChannelsConfigurationCommand;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.participation.ReportChannelConfigurationCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

public class ParticipationConfigurationCommandComposite extends CommandComposite{
	public ParticipationConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new IgnoredChannelsConfigurationCommand(this));
		this.addSubCommand(new ReportChannelConfigurationCommand(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Participation";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("participation");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return "Participation configurations";
	}
}
