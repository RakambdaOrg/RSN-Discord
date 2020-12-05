package fr.raksrinana.rsndiscord.modules.settings.command.guild;

import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.joinleave.ChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.joinleave.JoinImagesConfigurationCommand;
import fr.raksrinana.rsndiscord.modules.settings.command.guild.joinleave.LeaveImagesConfigurationCommand;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.modules.permission.PermissionUtils.ALLOW;

public class JoinLeaveConfigurationCommandComposite extends CommandComposite{
	public JoinLeaveConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new ChannelConfigurationCommand(this));
		this.addSubCommand(new JoinImagesConfigurationCommand(this));
		this.addSubCommand(new LeaveImagesConfigurationCommand(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "JoinLeave";
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return "Join/Leave configurations";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("joinLeave");
	}
}
