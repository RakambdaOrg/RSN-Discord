package fr.raksrinana.rsndiscord.command.impl.settings.guild.twitch;

import fr.raksrinana.rsndiscord.command.Command;
import fr.raksrinana.rsndiscord.command.impl.settings.helpers.ChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.permission.PermissionUtils.ALLOW;

public class TwitchChannelConfigurationCommand extends ChannelConfigurationCommand{
	public TwitchChannelConfigurationCommand(Command parent){
		super(parent);
	}
	
	@Override
	public @NotNull IPermission getPermission(){
		return ALLOW;
	}
	
	@Override
	protected void setConfig(@NotNull Guild guild, @NotNull ChannelConfiguration value){
		Settings.get(guild).getTwitchConfiguration().setTwitchChannel(value);
	}
	
	@Override
	protected void removeConfig(@NotNull Guild guild){
		Settings.get(guild).getTwitchConfiguration().setTwitchChannel(null);
	}
	
	@NotNull
	@Override
	protected Optional<ChannelConfiguration> getConfig(@NotNull Guild guild){
		return Settings.get(guild).getTwitchConfiguration().getTwitchChannel();
	}
	
	@NotNull
	@Override
	public String getName(@NotNull Guild guild){
		return "Twitch channel";
	}
	
	@NotNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("channel");
	}
}
