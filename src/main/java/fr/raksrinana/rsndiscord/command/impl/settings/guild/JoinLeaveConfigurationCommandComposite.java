package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.joinleave.ChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.joinleave.JoinImagesConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.joinleave.LeaveImagesConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

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
