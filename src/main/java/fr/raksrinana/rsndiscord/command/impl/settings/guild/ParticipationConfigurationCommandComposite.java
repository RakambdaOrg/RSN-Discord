package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.participation.IgnoredChannelsConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.participation.ReportChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class ParticipationConfigurationCommandComposite extends CommandComposite{
	public ParticipationConfigurationCommandComposite(Command parent){
		super(parent);
		addSubCommand(new IgnoredChannelsConfigurationCommand(this));
		addSubCommand(new ReportChannelConfigurationCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Participation";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("participation");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return "Participation configurations";
	}
}
