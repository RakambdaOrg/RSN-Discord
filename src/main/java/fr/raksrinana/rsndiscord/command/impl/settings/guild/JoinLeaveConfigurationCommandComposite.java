package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.joinleave.ChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.joinleave.JoinImagesConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.joinleave.LeaveImagesConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class JoinLeaveConfigurationCommandComposite extends CommandComposite{
	public JoinLeaveConfigurationCommandComposite(Command parent){
		super(parent);
		addSubCommand(new ChannelConfigurationCommand(this));
		addSubCommand(new JoinImagesConfigurationCommand(this));
		addSubCommand(new LeaveImagesConfigurationCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "JoinLeave";
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return "Join/Leave configurations";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("joinLeave");
	}
}
