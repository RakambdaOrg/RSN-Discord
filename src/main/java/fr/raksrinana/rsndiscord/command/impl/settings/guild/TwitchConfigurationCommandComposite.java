package fr.raksrinana.rsndiscord.command.impl.settings.guild;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.CommandComposite;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.twitch.AutoConnectUsersConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.twitch.IrcForwardConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.twitch.RandomKickRewardIdConfigurationCommand;
import fr.raksrinana.rsndiscord.command.impl.settings.guild.twitch.TwitchChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class TwitchConfigurationCommandComposite extends CommandComposite{
	public TwitchConfigurationCommandComposite(Command parent){
		super(parent);
		addSubCommand(new TwitchChannelConfigurationCommand(this));
		addSubCommand(new AutoConnectUsersConfigurationCommand(this));
		addSubCommand(new IrcForwardConfigurationCommand(this));
		addSubCommand(new RandomKickRewardIdConfigurationCommand(this));
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Twitch";
	}
	
	@NotNull
	@Override
	public @NotNull List<String> getCommandStrings(){
		return List.of("tw", "twitch");
	}
	
	@NotNull
	@Override
	public String getDescription(@NotNull Guild guild){
		return "Twitch configurations";
	}
}
