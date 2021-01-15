package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.twitch.AutoConnectUsersConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.twitch.IrcForwardConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.twitch.RandomKickRewardIdConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.twitch.TwitchChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class TwitchConfigurationCommandComposite extends CommandComposite{
	public TwitchConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new TwitchChannelConfigurationCommand(this));
		this.addSubCommand(new AutoConnectUsersConfigurationCommand(this));
		this.addSubCommand(new IrcForwardConfigurationCommand(this));
		this.addSubCommand(new RandomKickRewardIdConfigurationCommand(this));
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NonNull
	@Override
	public String getName(@NonNull Guild guild){
		return "Twitch";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("tw", "twitch");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return "Twitch configurations";
	}
}
