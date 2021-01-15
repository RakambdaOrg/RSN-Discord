package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.participation.IgnoredChannelsConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.participation.ReportChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

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
